/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.filter.support;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.HttpServerFilter;
import com.huawei.fit.http.server.HttpServerFilterChain;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.annotation.Scope;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.authentication.AuthenticationService;
import com.huawei.jade.authentication.context.HttpRequestUtils;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;

import java.util.Collections;
import java.util.List;

/**
 * 表示获取用户登录信息 http 请求过滤器。
 *
 * @author 陈潇文
 * @since 2024-07-30
 */
@Component
public class LoginFilter implements HttpServerFilter {
    private final List<String> matchPatterns;
    private final List<String> mismatchPatterns;
    private final AuthenticationService authenticationService;

    /**
     * 根据配置创建过滤器的实例。
     *
     * @param authenticationService 表示用户认证服务的 {@link AuthenticationService}。
     */
    public LoginFilter(AuthenticationService authenticationService) {
        // 待优化为配置化
        this.matchPatterns = Collections.singletonList("/**");
        this.mismatchPatterns = Collections.emptyList();
        this.authenticationService = Validation.notNull(authenticationService, "The auth service cannot be null.");
    }

    @Override
    public String name() {
        return "LoginFilter";
    }

    @Override
    public int priority() {
        return Order.HIGHEST;
    }

    @Override
    public List<String> matchPatterns() {
        return this.matchPatterns;
    }

    @Override
    public List<String> mismatchPatterns() {
        return this.mismatchPatterns;
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) {
        UserContext operationContext = new UserContext(this.authenticationService.getUserName(request),
                HttpRequestUtils.getUserIp(request), HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> chain.doFilter(request, response));
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }
}
