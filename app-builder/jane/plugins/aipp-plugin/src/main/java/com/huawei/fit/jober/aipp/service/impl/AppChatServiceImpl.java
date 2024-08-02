/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.QueryChatRsp;
import com.huawei.fit.jober.aipp.entity.ChatAndInstanceMap;
import com.huawei.fit.jober.aipp.entity.ChatInfo;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.AppBuilderAppService;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.service.AppChatService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.UUIDUtil;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.Tuple;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史会话服务实现类
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-23
 */
@Component
@RequiredArgsConstructor
public class AppChatServiceImpl implements AppChatService {
    private static final Logger LOGGER = Logger.get(AppChatServiceImpl.class);
    private final AppBuilderAppFactory appFactory;
    private final AippChatMapper aippChatMapper;
    private final AippRunTimeService aippRunTimeService;
    private final AppBuilderAppService appService;

    @Override
    public Choir<Object> chat(CreateAppChatRequest body, OperationContext context, boolean isDebug) {
        this.validateChatBody(body);
        this.convertContext(body);
        if (body.getChatId() != null) {
            // 如果已经存在chatId，表示为继续对话，需要将chatId往后传递，以便取出历史记录
            body.getContext().put("chatId", body.getChatId());
        }
        // 这里几行代码的顺序不可以调整，必须先把对话的appId查询出来，再去创建chatId
        String chatAppId = this.getAppId(body);
        boolean hasAtOtherApp = this.hasAtOtherApp(body);
        this.createChatId(body, hasAtOtherApp);
        // create instance —— 根据实际的那个app创建
        this.appService.updateFlow(chatAppId, context);
        Tuple tuple = this.aippRunTimeService.createInstanceByApp(chatAppId, body.getQuestion(), body.getContext(),
                context, isDebug);
        // 这两处不可能为null
        String instId = ObjectUtils.cast(tuple.get(0).orElse(null));
        Choir<Object> choir = ObjectUtils.cast(tuple.get(1).orElse(null));
        this.saveChatInfos(body, context, instId, hasAtOtherApp, chatAppId);
        return choir;
    }

    private void saveChatInfos(CreateAppChatRequest body, OperationContext context, String instId,
            boolean hasAtOtherApp, String chatAppId) {
        AppBuilderApp app = this.appFactory.create(body.getAppId());
        Map<String, String> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_CHAT_INST_ID_KEY, instId);
        attributes.put(AippConst.ATTR_CHAT_STATE_KEY, app.getState());
        String chatId = body.getChatId();
        this.buildAndInsertChatInfo(app, attributes, body.getQuestion(), chatId, context.getOperator());
        this.buildAndInsertWideRelationInfo(instId, chatId);
        if (hasAtOtherApp) {
            AppBuilderApp chatApp = this.appFactory.create(chatAppId);
            // 被@的应用的对话
            Map<String, String> originAttributes = new HashMap<>();
            originAttributes.put(AippConst.ATTR_CHAT_INST_ID_KEY, instId);
            originAttributes.put(AippConst.ATTR_CHAT_STATE_KEY, chatApp.getState());
            originAttributes.put(AippConst.ATTR_CHAT_ORIGIN_APP_KEY, app.getId());
            originAttributes.put(AippConst.ATTR_CHAT_ORIGIN_APP_VERSION_KEY, app.getVersion());
            String atChatId = ObjectUtils.cast(body.getContext().get(AippConst.BS_AT_CHAT_ID));
            this.buildAndInsertChatInfo(chatApp, originAttributes, body.getQuestion(), atChatId, context.getOperator());
            this.buildAndInsertWideRelationInfo(instId, atChatId);
        }
    }

    private void convertContext(CreateAppChatRequest body) {
        body.getContext().put(AippConst.BS_AIPP_USE_MEMORY_KEY, body.getContext().remove("use_memory"));
    }

    private String getAppId(CreateAppChatRequest body) {
        String atChatId = ObjectUtils.cast(body.getContext().get(AippConst.BS_AT_CHAT_ID));
        if (StringUtils.isNotBlank(atChatId)) {
            List<QueryChatRsp> chats = this.aippChatMapper.selectChatList(null, atChatId);
            if (CollectionUtils.isEmpty(chats)) {
                throw new AippException(AippErrCode.APP_CHAT_NOT_FOUND_BY_ID);
            }
            return chats.get(0).getAppId();
        }
        String atAppId = ObjectUtils.cast(body.getContext().get(AippConst.BS_AT_APP_ID));
        if (StringUtils.isNotBlank(atAppId)) {
            return atAppId;
        }
        return body.getAppId();
    }

    private boolean hasAtOtherApp(CreateAppChatRequest body) {
        String atChatId = ObjectUtils.cast(body.getContext().get(AippConst.BS_AT_CHAT_ID));
        String atAppId = ObjectUtils.cast(body.getContext().get(AippConst.BS_AT_APP_ID));
        return StringUtils.isNotBlank(atChatId) || StringUtils.isNotBlank(atAppId);
    }

    private void createChatId(CreateAppChatRequest body, boolean hasAtOtherApp) {
        // body里没有chatId：第一次对话
        if (StringUtils.isBlank(body.getChatId())) {
            body.setChatId(UUIDUtil.uuid());
        }
        // 没有被@的chatId， @其它应用的第一次对话
        if (hasAtOtherApp && StringUtils.isBlank(ObjectUtils.cast(body.getContext()
                .get(AippConst.BS_AT_CHAT_ID)))) {
            body.getContext().put(AippConst.BS_AT_CHAT_ID, UUIDUtil.uuid());
        }
        body.getContext().put(AippConst.BS_CHAT_ID, body.getChatId());
    }

    private void validateChatBody(CreateAppChatRequest body) {
        if (body == null || body.getContext() == null || StringUtils.isEmpty(body.getAppId())
                || StringUtils.isEmpty(body.getQuestion())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL);
        }
    }

    private void buildAndInsertChatInfo(AppBuilderApp app, Map<String, String> attributes, String chatName,
            String chatId, String operator) {
        String cutChatName = chatName.length() > 64 ? chatName.substring(0, 32) : chatName;
        LocalDateTime operateTime = LocalDateTime.now();
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(app.getId()).version(app.getVersion())
                .attributes(JsonUtils.toJsonString(attributes)).chatId(chatId).chatName(cutChatName)
                .status(AippConst.CHAT_STATUS).updater(operator).createTime(operateTime).updateTime(operateTime)
                .creator(operator).build();
        this.aippChatMapper.insertChat(chatInfo);
    }

    private void buildAndInsertWideRelationInfo(String instId, String chatId) {
        LocalDateTime operateTime = LocalDateTime.now();
        ChatAndInstanceMap wideRelationInfo = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid()).instanceId(instId).chatId(chatId)
                .createTime(operateTime).updateTime(operateTime).build();
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
    }
}