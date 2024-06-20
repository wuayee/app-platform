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
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;

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
public class StatisticsFilter implements GlobalFilter, Ordered {
    private static final String REQUEST_MODEL_NAME = "requestModelName";

    private static final String REQUEST_START_TIME = "requestStartTime";

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
     * @param chain {@link GatewayFilterChain}
     * @return {@link Mono<Void>}
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        log.info("Statistics filter: path=" + path);

        // path不会为空指针，空路径默认为"/"
        if (!path.contains("/v1/chat/completions") && !path.contains("/v1/embeddings")) {
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        DataBufferFactory dataBufferFactory = response.bufferFactory();

        exchange.getAttributes().put(REQUEST_START_TIME, System.currentTimeMillis());
        ServerHttpRequest decoratedRequest = updateStatsWithRequest(request, exchange);
        ServerHttpResponseDecorator decoratedResponse = updateStatsWithResponse(response, dataBufferFactory, exchange);
        return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                .then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(REQUEST_START_TIME);
            String model = exchange.getAttribute(REQUEST_MODEL_NAME);
            if (startTime == null || model == null || model.isEmpty()) {
                log.error("The start time or model is invalid.");
                return;
            }
            modelRouteService.updateModelPerformanceStatistics(model,
                    (double) (System.currentTimeMillis() - startTime) / 1000.0 /* 毫秒转换为秒 */);
        }));
    }

    private ServerHttpResponseDecorator updateStatsWithResponse(ServerHttpResponse response,
                                                                DataBufferFactory dataBufferFactory,
                                                                ServerWebExchange exchange) {
        return new ResponseBodyStatisticsReader(response, dataBufferFactory, exchange);
    }

    private ServerHttpRequest updateStatsWithRequest(ServerHttpRequest request, ServerWebExchange exchange) {
        return new RequestBodyStatisticsReader(request, exchange);
    }

    private class RequestBodyStatisticsReader extends ServerHttpRequestDecorator {
        private final ServerWebExchange exchange;

        /**
         * 请求体读取器的构造方法。
         *
         * @param request http 请求。
         * @param exchange 请求-响应交互上下文。
         */
        public RequestBodyStatisticsReader(ServerHttpRequest request, ServerWebExchange exchange) {
            super(request);
            this.exchange = exchange;
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return super.getBody().publishOn(Schedulers.boundedElastic()).doOnNext(dataBuffer -> {
                try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    Channels.newChannel(byteArrayOutputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                    processRequestBody(new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    log.error("Read request body error: " + e);
                }
            });
        }

        private void processRequestBody(String requestBody) {
            try {
                ModelStatistics modelStats = OBJECT_MAPPER.readValue(requestBody, ModelStatistics.class);
                if (modelStats == null) {
                    log.error("Failed to update stats, request body is null.");
                    return;
                }
                exchange.getAttributes().put(REQUEST_MODEL_NAME, modelStats.getModel());
                modelRouteService.updateModelWithRequestBody(modelStats);
            } catch (JsonProcessingException e) {
                log.error("Failed to update stats, read request body error: " + e);
            }
        }
    }

    private class ResponseBodyStatisticsReader extends ServerHttpResponseDecorator {
        private final ServerWebExchange exchange;
        private DataBufferFactory dataBufferFactory;

        private boolean isStreaming = false;

        private StringBuilder responseBodyBuilder = new StringBuilder();


        /**
         * 读取响应体中统计信息相关字段。
         *
         * @param response {@link ServerHttpResponse}
         * @param bufferFactory {@link DataBufferFactory}
         * @param exchange exchange
         */
        public ResponseBodyStatisticsReader(ServerHttpResponse response, DataBufferFactory bufferFactory,
                                            ServerWebExchange exchange) {
            super(response);
            this.dataBufferFactory = bufferFactory;
            this.exchange = exchange;
        }

        @Override
        public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
            if (!(body instanceof Flux)) {
                return super.writeWith(body);
            }
            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
            return super.writeWith(fluxBody.map(dataBuffer -> {
                byte[] content = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(content);
                DataBufferUtils.release(dataBuffer);
                String responseBody = new String(content, StandardCharsets.UTF_8);
                // 释放currentRoutes的请求连接数
                String modelName = String.valueOf(exchange.getAttributes().get(REQUEST_MODEL_NAME));
                Integer currentLinkNum = modelRouteService.getModelLinkControl().get(modelName);
                if (currentLinkNum != null) {
                    currentLinkNum += 1;
                    log.info("Update modelLinkControl for " + modelName
                            + " in response process, link num to: " + currentLinkNum);
                    modelRouteService.getModelLinkControl().put(modelName, currentLinkNum);
                }
                if (this.isStreaming) {
                    processStreamingResponse(responseBody);
                } else {
                    processSyncResponse(responseBody);
                }
                return dataBufferFactory.wrap(content);
            }));
        }

        /**
         * 拦截并处理响应，最后将响应写回，服务端返回多个响应时会调用此方法（比如大模型流式请求）。
         *
         * @param body 响应体。
         * @return 处理后的响应体。
         */
        @Override
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            this.isStreaming = true;
            return writeWith(Flux.from(body).flatMapSequential(p -> p));
        }

        private void processSyncResponse(String responseBody) {
            try {
                modelRouteService.updateModelWithResponseBody(
                        OBJECT_MAPPER.readValue(responseBody, ModelStatistics.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to update stats, read response body error: " + e);
            }
        }

        private void processStreamingResponse(String responseChunk) {
            if (responseChunk == null || responseChunk.isEmpty()) {
                log.error("The response body is empty");
                return;
            }

            this.responseBodyBuilder.append(responseChunk.replaceAll("\n", ""));
            String responseBody = responseBodyBuilder.toString();
            if (!responseBody.endsWith("[DONE]")) {
                return;
            }

            String[] data = responseBody.split("data: ");
            String lastChunk = data[data.length - 2]; // [DONE]标识符前的一个chunk携带usage字段
            try {
                modelRouteService.updateModelWithResponseBody(
                        OBJECT_MAPPER.readValue(lastChunk, ModelStatistics.class));
            } catch (JsonProcessingException e) {
                log.error("Failed to update stats, read response body error: " + e);
            }
        }
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }
}