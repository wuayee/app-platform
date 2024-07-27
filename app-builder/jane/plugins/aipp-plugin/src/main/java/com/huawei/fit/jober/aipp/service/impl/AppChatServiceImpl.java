/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRsp;
import com.huawei.fit.jober.aipp.entity.ChatAndInstanceMap;
import com.huawei.fit.jober.aipp.entity.ChatInfo;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.service.AppChatService;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.UUIDUtil;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
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

    private final AppBuilderAppMapper appMapper;

    private final AippChatMapper aippChatMapper;

    private final AippRunTimeService aippRunTimeService;

    @Override
    public CreateAppChatRsp chat(CreateAppChatRequest body, OperationContext context) {
        this.validateChatBody(body);
        if (body.getChatId() != null) {
            // 如果已经存在chatId，表示为继续对话，需要将chatId往后传递，以便取出历史记录
            body.getInitContext().put("chatId", body.getChatId());
        }
        // app
        AppBuilderAppPO app = this.appMapper.selectWithId(body.getAppId());
        // chatId
        String chatId = body.getChatId() == null ? UUIDUtil.uuid() : body.getChatId();
        // create instance
        String instId = this.aippRunTimeService.createInstanceByApp(body.getAppId(),
                body.getQuestion(), body.getInitContext(), context);
        // attributes
        Map<String, String> attributes = new HashMap<>();
        attributes.put(AippConst.ATTR_CHAT_INST_ID_KEY, instId);
        attributes.put(AippConst.ATTR_CHAT_STATE_KEY, app.getState());
        // operateTime
        this.buildAndInsertChatInfo(app, attributes, body.getQuestion(), chatId, context.getOperator());
        this.buildAndInsertWideRelationInfo(instId, chatId);
        return CreateAppChatRsp.builder().chatId(chatId).build();
    }

    private void validateChatBody(CreateAppChatRequest body) {
        if (body == null || body.getInitContext() == null || StringUtils.isEmpty(body.getAppId())
                || StringUtils.isEmpty(body.getQuestion())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL);
        }
    }

    private void buildAndInsertChatInfo(AppBuilderAppPO app, Map<String, String> attributes, String chatName,
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