/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.support.GatewayToStringStyler;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 将请求头中 Authorization 字段重置为网关对应路由中配置的模型接口密钥。
 *
 * @author 张庭怿
 * @since 2024-05-31
 */
@Component
@Slf4j
public class ReplaceHeaderGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    /**
     * 根据配置生成过滤器。
     *
     * @param config 过滤器配置。
     * @return {@link GatewayFilter}
     */
    public GatewayFilter apply(final AbstractNameValueGatewayFilterFactory.NameValueConfig config) {
        return new GatewayFilter() {
            /**
             * 过滤器实现：在请求头中 Authorization 字段设置路由配置中的 api key。
             *
             * @param exchange {@link ServerWebExchange}
             * @param chain {@link GatewayFilterChain}
             * @return {@link Mono}
             */
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String value = ServerWebExchangeUtils.expand(exchange, config.getValue());
                ServerHttpRequest request = exchange.getRequest()
                        .mutate()
                        .headers((httpHeaders) -> addApiKeyToHeaders(httpHeaders, config.getName(), value))
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            }

            /**
             * 将 {@link ReplaceHeaderGatewayFilterFactory} 生成的过滤器转换为字符串。
             *
             * @return 本过滤器的字符串表示。
             */
            public String toString() {
                return GatewayToStringStyler.filterToStringCreator(
                        ReplaceHeaderGatewayFilterFactory.this).append(config.getName(), config.getValue()).toString();
            }
        };
    }

    private void addApiKeyToHeaders(HttpHeaders httpHeaders, String key, String value) {
        if (httpHeaders.containsKey(key)) {
            log.warn("The request has " + key + " header: " + httpHeaders.get(key));
        }
        // 使用网关路由中配置的api key覆盖请求头中Authorization字段
        log.info("Set " + key + " header to: " + value);
        httpHeaders.set(key, value);
    }
}
