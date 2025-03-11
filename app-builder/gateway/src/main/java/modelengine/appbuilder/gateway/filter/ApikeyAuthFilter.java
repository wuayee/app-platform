/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.filter;

import com.huawei.framework.crypt.grpc.client.exception.CryptoInvokeException;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import modelengine.appbuilder.gateway.service.ApikeyAuthService;
import modelengine.appbuilder.gateway.utils.UserUtil;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * Apikey 鉴权过滤器的构造工厂类。
 *
 * @author 李智超
 * @since 2024-12-11
 */
@Slf4j
@Component
public class ApikeyAuthFilter extends AbstractGatewayFilterFactory<ApikeyAuthFilter.Config> {
    private static final int APIKEY_AUTH_FILTER_ORDER = -1;
    private static final String USER_NAME_PREFIX = "sys_api_";
    private static final int ME_SK_START_POS = 13;
    private static final int ME_SK_END_POS = 21;

    private final ApikeyAuthService apikeyAuthService;

    ApikeyAuthFilter(ApikeyAuthService service) {
        super(Config.class);
        this.apikeyAuthService = service;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter(this::filter, APIKEY_AUTH_FILTER_ORDER);
    }

    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("Received request with Authorization token.");

        if (token == null || !apikeyAuthService.authApikeyInfo(token)) {
            // 认证失败，返回 401 错误
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            log.error("Authentication failed: Token is null or invalid.");
            return exchange.getResponse().setComplete();
        }

        String userName = this.generateUniqueNameForApiKey(token);
        ServerHttpRequest newRequest;
        try {
            newRequest = UserUtil.buildNewRequestBuilder(exchange, userName).build();
        } catch (CryptoInvokeException e) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
            log.error("Authentication failed: Failed to generate user info by api key auth.");
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    private String generateUniqueNameForApiKey(String apiKey) {
        return USER_NAME_PREFIX + apiKey.substring(ME_SK_START_POS, ME_SK_END_POS);
    }

    /**
     * Apikey 鉴权过滤器的构造工厂类的配置类。
     *
     * @author 李智超
     * @since 2024-12-11
     */
    @Data
    @NoArgsConstructor
    public static class Config {}
}
