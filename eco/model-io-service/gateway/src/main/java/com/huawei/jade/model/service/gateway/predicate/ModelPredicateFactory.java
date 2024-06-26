/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.predicate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * 根据请求体中 model 字段的值进行断言。
 *
 * @author 张庭怿
 * @since 2024-05-09
 */
@Component
@Slf4j
public class ModelPredicateFactory extends AbstractRoutePredicateFactory<ModelPredicateFactory.Config> {
    private static final String CACHE_REQUEST_BODY_OBJECT = "cachedRequestBodyObject";

    private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies
            .withDefaults().messageReaders();

    public ModelPredicateFactory() {
        super(ModelPredicateFactory.Config.class);
    }

    public ModelPredicateFactory(Class<ModelPredicateFactory.Config> configClass) {
        super(configClass);
    }

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(ModelPredicateFactory.Config config) {
        return exchange -> {
            Object cachedBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT);

            if (cachedBody == null) {
                return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange,
                        (serverHttpRequest) -> ServerRequest
                                .create(exchange.mutate().request(serverHttpRequest).build(), messageReaders)
                                .bodyToMono(ModelRequest.class)
                                .doOnNext(objectValue ->
                                        exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT, objectValue))
                                .map(objectValue -> compare(config.model, objectValue)));
            }

            if (cachedBody instanceof ModelRequest) {
                ModelRequest request = (ModelRequest) cachedBody;
                return Mono.just(compare(config.model, request));
            }

            return Mono.just(false);
        };
    }

    private boolean compare(String model, ModelRequest request) {
        if (request == null) {
            log.error("Failed to compare model {}, the request is null", model);
            return false;
        }

        String task = request.getTask();
        if (task == null || task.isEmpty()) {
            return Objects.equals(model, request.getModel());
        } else {
            String pipeline = request.getModel().replace('/', '-') + "-" + request.getTask();
            pipeline = pipeline.toLowerCase(Locale.ENGLISH);
            log.info("Compare pipeline={} with model={}", pipeline, model);
            return pipeline.equals(model);
        }
    }

    @Override
    public Predicate<ServerWebExchange> apply(ModelPredicateFactory.Config config) {
        throw new UnsupportedOperationException("ModelPredicateFactory only supports applyAsync.");
    }

    /**
     * 此类的成员表示配置文件中可配置的字段。
     */
    @Data
    public static class Config {
        private String model;
    }

    /**
     * 此类表示客户端发送的请求（仅需解析 model 字段）。
     */
    @Data
    public static class ModelRequest {
        private String task;

        private String model;
    }
}
