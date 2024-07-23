/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.filter;

import com.huawei.jade.model.service.gateway.service.ModifyModelResponseService;

import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 修改模型服务回应过滤器。
 *
 * @author 王浩冉
 * @since 2024-07-15
 */
@Component
public class ModifyModelResponseFilter implements GlobalFilter, Ordered {
    private ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilter;

    private ModifyModelResponseService rewriteFunction;

    public ModifyModelResponseFilter(ModifyResponseBodyGatewayFilterFactory filter,
            ModifyModelResponseService function) {
        this.modifyResponseBodyFilter = filter;
        this.rewriteFunction = function;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return this.modifyResponseBodyFilter
                .apply(new ModifyResponseBodyGatewayFilterFactory.Config()
                        .setRewriteFunction(String.class, String.class, this.rewriteFunction))
                .filter(exchange, chain);
    }

    @Override
    public int getOrder() {
        return StatisticsFilter.STATISTICS_FILTER_ORDER - 1;
    }
}
