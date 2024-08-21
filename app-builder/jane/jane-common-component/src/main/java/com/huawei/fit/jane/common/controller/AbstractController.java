/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.controller;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jane.task.gateway.User;
import modelengine.fitframework.util.StringUtils;
import com.huawei.jade.authentication.context.HttpRequestUtils;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;

/**
 * 为 Controller 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public abstract class AbstractController {
    private final Authenticator authenticator;

    public AbstractController(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    private static String getSourcePlatform(HttpClassicServerRequest request) {
        return request.headers().first("SourcePlatform").orElse(StringUtils.EMPTY);
    }

    /**
     * 获取操作上下文。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示操作上下文的 {@link OperationContext}。
     */
    public OperationContext contextOf(HttpClassicServerRequest request, String tenantId) {
        UserContext userContext = UserContextHolder.get();
        String ip = (userContext == null) ? HttpRequestUtils.getUserIp(request) : userContext.getIp();
        String language =
                (userContext == null) ? HttpRequestUtils.getAcceptLanguages(request) : userContext.getLanguage();
        User user = this.authenticator.authenticate(request);
        if (user == null) {
            return new OperationContext(tenantId, null, null, null, null, null, ip, getSourcePlatform(request),
                    language);
        }
        return new OperationContext(tenantId, user.fqn(), null, user.account(), null, user.name(), ip,
                getSourcePlatform(request), language);
    }
}
