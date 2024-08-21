/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.controller.AbstractController;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.service.AppChatService;
import com.huawei.fit.jober.aipp.service.impl.AppChatServiceImpl;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * app对话管理接口
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "app对话管理接口")
public class AppChatController extends AbstractController {
    private static final Logger LOGGER = Logger.get(AppChatServiceImpl.class);
    private final AppChatService appChatService;

    /**
     * 构造方法
     *
     * @param authenticator 身份校验器
     * @param appChatService 对话服务
     */
    public AppChatController(Authenticator authenticator, AppChatService appChatService) {
        super(authenticator);
        this.appChatService = appChatService;
    }

    /**
     * 会话接口
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param body 会话参数
     * @return SSE流
     */
    @PostMapping(value = "/app_chat", description = "会话接口，传递会话信息")
    public Choir<Object> chat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody CreateAppChatRequest body) {
        this.validateChatBody(body);
        this.validateChatQuestion(body);
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), false);
    }

    /**
     * debug会话接口
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param body 会话参数
     * @return SSE流
     */
    @PostMapping(value = "/app_chat_debug", description = "会话接口，传递会话信息")
    public Choir<Object> chatDebug(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody CreateAppChatRequest body) {
        this.validateChatBody(body);
        this.validateChatQuestion(body);
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), true);
    }

    /**
     * 工具流debug会话接口
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param body 会话参数
     * @return SSE流
     */
    @PostMapping(value = "/water_flow_chat", description = "会话接口，传递会话信息")
    public Choir<Object> waterFlowChat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody CreateAppChatRequest body) {
        this.validateChatBody(body);
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), false);
    }

    /**
     * 工具流debug会话接口
     *
     * @param httpRequest 请求
     * @param tenantId 租户id
     * @param body 会话参数
     * @return SSE流
     */
    @PostMapping(value = "/water_flow_chat_debug", description = "会话接口，传递会话信息")
    public Choir<Object> waterFlowChatDebug(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody CreateAppChatRequest body) {
        this.validateChatBody(body);
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), true);
    }

    /**
     * 重新发起会话。
     *
     * @param httpRequest Http 请求体。
     * @param currentInstanceId 需要重新发起会话的实例 ID。
     * @param tenantId 租户 ID。
     * @param additionalContext 重新会话需要的信息，如是否使用多轮对话等等。
     * @return 表示会话相应体的 sse流。
     */
    @PostMapping(path = "/instances/{current_instance_id}", description = "重新发起会话接口")
    public Choir<Object> restartChat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("current_instance_id") String currentInstanceId,
            @RequestBody Map<String, Object> additionalContext) {
        return this.appChatService.restartChat(currentInstanceId, additionalContext,
                this.contextOf(httpRequest, tenantId));
    }

    private void validateChatBody(CreateAppChatRequest body) {
        if (body == null || body.getContext() == null || StringUtils.isEmpty(body.getAppId())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL);
        }
    }

    private void validateChatQuestion(CreateAppChatRequest body) {
        if (StringUtils.isEmpty(body.getQuestion())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_QUESTION_IS_NULL);
        }
    }
}
