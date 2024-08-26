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
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.entity.ChatAndInstanceMap;
import com.huawei.fit.jober.aipp.entity.ChatInfo;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.enums.AppState;
import com.huawei.fit.jober.aipp.enums.RestartModeEnum;
import com.huawei.fit.jober.aipp.factory.AppBuilderAppFactory;
import com.huawei.fit.jober.aipp.genericable.AppBuilderAppService;
import com.huawei.fit.jober.aipp.mapper.AippChatMapper;
import com.huawei.fit.jober.aipp.repository.AppBuilderAppRepository;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AippRunTimeService;
import com.huawei.fit.jober.aipp.service.AppChatService;
import com.huawei.fit.jober.aipp.util.AippLogUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.aipp.util.UUIDUtil;
import com.huawei.fit.jober.common.ServerInternalException;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.model.Tuple;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史会话服务实现类
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Component
@RequiredArgsConstructor
public class AppChatServiceImpl implements AppChatService {
    private static final int FROM_OTHER_CHAT = 2;
    private static final String DEFAULT_CHAT_NAME_PREFIX = "@appBuilderDebug-";

    private final AppBuilderAppFactory appFactory;
    private final AippChatMapper aippChatMapper;
    private final AippRunTimeService aippRunTimeService;
    private final AppBuilderAppService appService;
    private final AippLogService aippLogService;
    private final AppBuilderAppRepository appRepository;

    @Override
    public Choir<Object> chat(CreateAppChatRequest body, OperationContext context, boolean isDebug) {
        this.validateApp(body.getAppId());
        Map<String, Object> businessData = this.convertContextToBusinessData(body);
        // 这里几行代码的顺序不可以调整，必须先把对话的appId查询出来，再去创建chatId
        String chatAppId = this.getAppId(body);
        boolean hasAtOtherApp = this.hasAtOtherApp(body);
        this.createChatId(body, hasAtOtherApp, businessData);
        // create instance —— 根据实际的那个app创建
        this.appService.updateFlow(chatAppId, context);
        Tuple tuple = this.aippRunTimeService.createInstanceByApp(chatAppId, body.getQuestion(), businessData,
                context, isDebug);
        // 这tuple的两个值都不可能为null
        this.saveChatInfos(body, context, ObjectUtils.cast(tuple.get(0).orElseThrow(this::generalServerException)),
                hasAtOtherApp, chatAppId);
        return ObjectUtils.cast(tuple.get(1).orElseThrow(this::generalServerException));
    }

    @Override
    public Choir<Object> restartChat(String instanceId, Map<String, Object> additionalContext,
            OperationContext operationContext) {
        String path = this.aippLogService.getParentPath(instanceId);
        String parentInstanceId = path.split(AippLogUtils.PATH_DELIMITER)[1];
        if (StringUtils.isEmpty(parentInstanceId)) {
            throw new AippException(AippErrCode.PARENT_INSTANCE_ID_NOT_FOUND, instanceId);
        }
        // 这个方法查询的chatList，0号位一定是原对话，1号位一定是at对话（如果有的话），详见本类saveChatInfo方法
        List<QueryChatRsp> chatList = this.aippChatMapper.selectChatListByInstId(parentInstanceId);
        if (CollectionUtils.isEmpty(chatList)) {
            throw new AippParamException(AippErrCode.CHAT_NOT_FOUND_BY_INSTANCE_ID, parentInstanceId);
        }
        String restartMode = ObjectUtils.cast(additionalContext.getOrDefault(AippConst.RESTART_MODE,
                RestartModeEnum.OVERWRITE.getMode()));
        additionalContext.put(AippConst.RESTART_MODE, restartMode);
        CreateAppChatRequest body = this.buildChatBody(parentInstanceId, additionalContext, chatList);
        if (StringUtils.equals(RestartModeEnum.OVERWRITE.getMode(), restartMode)) {
            this.aippChatMapper.deleteWideRelationshipByInstanceId(parentInstanceId);
            this.aippLogService.deleteInstanceLog(parentInstanceId);
        }
        boolean isDebug = AppState.INACTIVE.getName()
                .equals(JsonUtils.parseObject(chatList.get(0).getAttributes()).get(AippConst.ATTR_CHAT_STATE_KEY));
        return this.chat(body, operationContext, isDebug);
    }

    private CreateAppChatRequest buildChatBody(String parentInstanceId, Map<String, Object> additionalContext,
            List<QueryChatRsp> chatList) {
        CreateAppChatRequest.CreateAppChatRequestBuilder bodyBuilder = CreateAppChatRequest.builder();
        List<AippInstLog> instLogs = this.aippLogService.queryLogsByInstanceIdAndLogTypes(parentInstanceId,
                Arrays.asList(AippInstLogType.QUESTION.name(), AippInstLogType.HIDDEN_QUESTION.name()));
        AippInstLog questionLog = instLogs.get(0);
        String question = ObjectUtils.cast(JsonUtils.parseObject(questionLog.getLogData()).get("msg"));
        bodyBuilder.question(question);
        bodyBuilder.chatId(chatList.get(0).getChatId());
        bodyBuilder.appId(chatList.get(0).getAppId());
        CreateAppChatRequest.Context.ContextBuilder contextBuilder = CreateAppChatRequest.Context.builder();
        contextBuilder.userContext(additionalContext);
        if (chatList.size() == FROM_OTHER_CHAT) {
            contextBuilder.atChatId(chatList.get(1).getChatId());
        }
        return bodyBuilder.context(contextBuilder.build()).build();
    }

    private void validateApp(String appId) {
        AppBuilderApp appBuilderApp = this.appRepository.selectWithId(appId);
        if (appBuilderApp == null || StringUtils.isEmpty(appBuilderApp.getId())) {
            throw new AippException(AippErrCode.APP_NOT_FOUND_WHEN_CHAT);
        }
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
            String atChatId = body.getContext().getAtChatId();
            this.buildAndInsertChatInfo(chatApp, originAttributes, body.getQuestion(), atChatId, context.getOperator());
            this.buildAndInsertWideRelationInfo(instId, atChatId);
        }
    }

    private Map<String, Object> convertContextToBusinessData(CreateAppChatRequest body) {
        Map<String, Object> businessData = new HashMap<>();
        if (body.getContext().getUseMemory() != null) {
            businessData.put(AippConst.BS_AIPP_USE_MEMORY_KEY, body.getContext().getUseMemory());
        }
        businessData.put("dimension", body.getContext().getDimension());
        if (MapUtils.isNotEmpty(body.getContext().getUserContext())) {
            businessData.putAll(body.getContext().getUserContext());
        }
        return businessData;
    }

    private String getAppId(CreateAppChatRequest body) {
        String atChatId = body.getContext().getAtChatId();
        if (StringUtils.isNotBlank(atChatId)) {
            List<QueryChatRsp> chats = this.aippChatMapper.selectChatList(null, atChatId);
            if (CollectionUtils.isEmpty(chats)) {
                throw new AippException(AippErrCode.APP_CHAT_NOT_FOUND_BY_ID);
            }
            return chats.get(0).getAppId();
        }
        String atAppId = body.getContext().getAtAppId();
        if (StringUtils.isNotBlank(atAppId)) {
            return atAppId;
        }
        return body.getAppId();
    }

    private boolean hasAtOtherApp(CreateAppChatRequest body) {
        return StringUtils.isNotBlank(body.getContext().getAtChatId()) || StringUtils.isNotBlank(body.getContext()
                .getAtAppId());
    }

    private void createChatId(CreateAppChatRequest body, boolean hasAtOtherApp, Map<String, Object> businessData) {
        // body里没有chatId：第一次对话
        if (StringUtils.isBlank(body.getChatId())) {
            body.setChatId(UUIDUtil.uuid());
        }
        // 没有被@的chatId， @其它应用的第一次对话
        if (hasAtOtherApp && StringUtils.isBlank((body.getContext().getAtChatId()))) {
            body.getContext().setAtChatId(UUIDUtil.uuid());
            businessData.put(AippConst.BS_AT_CHAT_ID, body.getContext().getAtChatId());
        }
        businessData.put(AippConst.BS_CHAT_ID, body.getChatId());
    }

    private void buildAndInsertChatInfo(AppBuilderApp app, Map<String, String> attributes, String chatName,
            String chatId, String operator) {
        String cutChatName = this.generateChatName(chatName);
        LocalDateTime operateTime = LocalDateTime.now();
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(app.getId()).version(app.getVersion())
                .attributes(JsonUtils.toJsonString(attributes)).chatId(chatId).chatName(cutChatName)
                .status(AippConst.CHAT_STATUS).updater(operator).createTime(operateTime).updateTime(operateTime)
                .creator(operator).build();
        this.aippChatMapper.insertChat(chatInfo);
    }

    private String generateChatName(String chatName) {
        if (chatName == null) {
            return DEFAULT_CHAT_NAME_PREFIX + UUIDUtil.uuid().substring(0, 6);
        }
        return chatName.length() > 64 ? chatName.substring(0, 32) : chatName;
    }

    private void buildAndInsertWideRelationInfo(String instId, String chatId) {
        LocalDateTime operateTime = LocalDateTime.now();
        ChatAndInstanceMap wideRelationInfo = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid()).instanceId(instId).chatId(chatId)
                .createTime(operateTime).updateTime(operateTime).build();
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
    }

    private ServerInternalException generalServerException() {
        return new ServerInternalException("Except no null value but null!");
    }
}