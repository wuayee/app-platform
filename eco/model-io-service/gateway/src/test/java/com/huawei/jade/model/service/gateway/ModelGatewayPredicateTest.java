/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.jade.model.service.gateway.predicate.ModelPredicateFactory;

import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

/**
 * 网关断言基础测试。
 *
 * @since 2024-05-09
 * @author 张庭怿
 */
public class ModelGatewayPredicateTest {
    private static final String MODEL_NAME = "test";

    @Test
    public void testPredicateMatched() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/")
                .header("Content-Type", "application/json")
                .body("{\"model\": \"" + MODEL_NAME + "\"}");

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Boolean> result = (Mono<Boolean>) getTestAsyncPredicate(MODEL_NAME).apply(exchange);
        assertEquals(true, result.block());

        // 测试请求体已缓存场景下断言结果
        result = (Mono<Boolean>) getTestAsyncPredicate(MODEL_NAME).apply(exchange);
        assertEquals(true, result.block());
    }

    @Test
    public void testPredicateUnmatched() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/")
                .header("Content-Type", "application/json")
                .body("{\"model\": \"" + MODEL_NAME + "fake" + "\"}");

        ServerWebExchange exchange = MockServerWebExchange.from(request);
        Mono<Boolean> result = (Mono<Boolean>) getTestAsyncPredicate(MODEL_NAME).apply(exchange);
        assertEquals(false, result.block());
    }

    private AsyncPredicate<ServerWebExchange> getTestAsyncPredicate(String modelName) {
        ModelPredicateFactory predicate = new ModelPredicateFactory();
        ModelPredicateFactory.Config config = new ModelPredicateFactory.Config();
        config.setModel(modelName);
        return predicate.applyAsync(config);
    }
}
