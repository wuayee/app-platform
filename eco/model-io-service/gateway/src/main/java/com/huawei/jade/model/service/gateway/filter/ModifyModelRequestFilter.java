/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.filter;

import com.huawei.jade.model.service.gateway.service.ModifyModelRequestService;

import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 修改模型服务请求体过滤器。
 *
 * @author 张庭怿
 * @since 2024-07-11
 */
@Component
public class ModifyModelRequestFilter implements GlobalFilter, Ordered {
    private ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilter;

    private ModifyModelRequestService rewriteFunction;

    public ModifyModelRequestFilter(ModifyRequestBodyGatewayFilterFactory filter, ModifyModelRequestService function) {
        this.modifyRequestBodyFilter = filter;
        this.rewriteFunction = function;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return this.modifyRequestBodyFilter
                .apply(new ModifyRequestBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(String.class, String.class, this.rewriteFunction))
                .filter(exchange, chain);
    }

    @Override
    public int getOrder() {
        return StatisticsFilter.STATISTICS_FILTER_ORDER - 1;
    }
}
