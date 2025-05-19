/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.appbuilder.gateway.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 公共的 filter 过滤器
 *
 * @author 邬涨财
 * @since 2025-01-15
 */
@Slf4j
@Component
public class CommonFilter extends AbstractGatewayFilterFactory<CommonFilter.Config> {
    private static final int FILTER_ORDER = -3;

    CommonFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        log.info("CommonFilter is apply");
        return new OrderedGatewayFilter(this::filter, FILTER_ORDER);
    }

    private Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        request.getHeaders().set("Connection", "close");
        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * CommonFilter 构造工厂类的配置类。
     *
     * @author 邬涨财
     * @since 2025-01-02
     */
    @Data
    @NoArgsConstructor
    public static class Config {}
}
