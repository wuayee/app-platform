/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.filter;

import com.huawei.jade.model.service.gateway.entity.ModelStatistics;
import com.huawei.jade.model.service.gateway.service.ModelStatisticsService;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

/**
 * 此过滤器用于读取大模型请求、响应体中的数据，并更新对应模型的统计信息。
 *
 * @author 张庭怿
 * @since 2024-05-20
 */
@Configuration
@Slf4j
public class StatisticsFilter implements WebFilter {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private ModelStatisticsService modelRouteService;

    public StatisticsFilter(ModelStatisticsService service) {
        this.modelRouteService = service;
    }

    /**
     * 过滤器构造函数。
     *
     * @param exchange {@link ServerWebExchange}
     * @param chain {@link WebFilterChain}
     * @return {@link Mono<Void>}
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        if (!"/v1/chat/completions".equals(path) && !"/v1/embeddings".equals(path)) {
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        DataBufferFactory dataBufferFactory = response.bufferFactory();

        ServerHttpRequest decoratedRequest = updateStatsWithRequest(request);
        ServerHttpResponseDecorator decoratedResponse = updateStatsWithResponse(response, dataBufferFactory);
        return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build());
    }

    private ServerHttpResponseDecorator updateStatsWithResponse(ServerHttpResponse response,
                                                                DataBufferFactory dataBufferFactory) {
        return new ResponseBodyStatisticsReader(response, dataBufferFactory);
    }

    private ServerHttpRequest updateStatsWithRequest(ServerHttpRequest request) {
        return new RequestBodyStatisticsReader(request);
    }

    private class RequestBodyStatisticsReader extends ServerHttpRequestDecorator {
        public RequestBodyStatisticsReader(ServerHttpRequest request) {
            super(request);
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return super.getBody().publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                    String requestBody = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
                    try {
                        modelRouteService.updateModelWithRequestBody(
                                OBJECT_MAPPER.readValue(requestBody, ModelStatistics.class));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to update stats, read response body error: " + e);
                    }
                } catch (IOException e) {
                    log.error("Read request body error: " + e);
                }
            });
        }
    }

    private class ResponseBodyStatisticsReader extends ServerHttpResponseDecorator {
        private DataBufferFactory dataBufferFactory;

        /**
         * 读取响应体中统计信息相关字段。
         *
         * @param response {@link ServerHttpResponse}
         * @param bufferFactory {@link DataBufferFactory}
         */
        public ResponseBodyStatisticsReader(ServerHttpResponse response, DataBufferFactory bufferFactory) {
            super(response);
            this.dataBufferFactory = bufferFactory;
        }

        @Override
        public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
            if (!(body instanceof Flux)) {
                return super.writeWith(body);
            }
            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
            return super.writeWith(fluxBody.buffer().handle((dataBuffers, sink) -> {
                DefaultDataBuffer joinedBuffers = new DefaultDataBufferFactory().join(dataBuffers);
                byte[] content = new byte[joinedBuffers.readableByteCount()];
                joinedBuffers.read(content);
                String responseBody = new String(content, StandardCharsets.UTF_8);

                try {
                    modelRouteService.updateModelWithResponseBody(
                            OBJECT_MAPPER.readValue(responseBody, ModelStatistics.class));
                } catch (JsonProcessingException e) {
                    log.error("Failed to update stats, read request body error: " + e);
                }
                sink.next(this.dataBufferFactory.wrap(content));
            })).onErrorResume(err -> {
                log.error("error while decorating Response: {}", err.getMessage());
                return Mono.empty();
            });
        }
    }
}