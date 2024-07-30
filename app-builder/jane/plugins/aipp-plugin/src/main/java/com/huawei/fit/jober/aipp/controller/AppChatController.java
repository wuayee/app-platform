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
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.service.AppChatService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.flowable.Choir;

/**
 * app对话管理接口
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-23
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}", group = "app对话管理接口")
public class AppChatController extends AbstractController {
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
    public Choir<Object> chat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody CreateAppChatRequest body) {
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
    public Choir<Object> chatDebug(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody CreateAppChatRequest body) {
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), true);
    }
}
