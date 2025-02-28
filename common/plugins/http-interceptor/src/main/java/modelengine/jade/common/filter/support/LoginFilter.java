/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.inspection.Validation;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

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
                HttpRequestUtils.getUserIp(request),
                HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> chain.doFilter(request, response));
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }
}
