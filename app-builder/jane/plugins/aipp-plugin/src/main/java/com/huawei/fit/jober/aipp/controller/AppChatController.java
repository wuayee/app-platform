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
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import com.huawei.fit.jober.aipp.dto.chat.CreateAppChatRsp;
import com.huawei.fit.jober.aipp.service.AppChatService;
import com.huawei.fitframework.annotation.Component;

/**
 * app对话管理接口
 *
 * @author 姚江 yWX1299574
 * @since 2024-07-23
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/appChat", group = "app对话管理接口")
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
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param body body
     * @return Rsp<CreateAppChatRsp>
     */
    @PostMapping(description = "会话接口，传递会话信息")
    public Rsp<CreateAppChatRsp> chat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody CreateAppChatRequest body) {
        return Rsp.ok(appChatService.chat(body, this.contextOf(httpRequest, tenantId)));
    }
}
