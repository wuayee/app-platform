/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于修改访问大模型服务请求的请求体。
 *
 * @author 张庭怿
 * @since 2024-07-11
 */
@Service
@Slf4j
public class ModifyModelRequestService implements RewriteFunction<String, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Getter
    private Map<String, Integer> maxTokens = new ConcurrentHashMap<>();

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        try {
            String path = exchange.getRequest().getPath().toString();
            if (!path.contains("/v1/chat/completions")) {
                log.warn("Skip endpoint: " + path);
                return Mono.justOrEmpty(body);
            }

            Map<String, Object> requestBody = OBJECT_MAPPER.readValue(body, Map.class);
            modifyMaxTokens(requestBody);
            modifyTemperature(requestBody);

            return Mono.justOrEmpty(OBJECT_MAPPER.writeValueAsString(requestBody));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to modify model request:" + e);
        }
    }

    private void modifyMaxTokens(Map<String, Object> requestBody) {
        if ("disabled".equals(System.getenv("FILL_MAX_TOKENS"))) {
            log.warn("Skip model request modification");
            return;
        }

        String modelName = null;
        if (requestBody.get("model") instanceof String) {
            modelName = (String) requestBody.get("model");
        }

        if (!requestBody.containsKey("max_tokens") && modelName != null
                && this.maxTokens.containsKey(modelName) && this.maxTokens.get(modelName) != null) {
            Integer defaultMaxTokens = this.maxTokens.get(modelName);
            requestBody.put("max_tokens", defaultMaxTokens);
            log.info("Set max_tokens={} for model {}", defaultMaxTokens, modelName);
        }
    }

    private void modifyTemperature(Map<String, Object> requestBody) {
        final double minTemperature = 0.01d;
        if (requestBody.get("temperature") instanceof Number) {
            if (((Number) requestBody.get("temperature")).doubleValue() < minTemperature) {
                requestBody.put("temperature", minTemperature);
                log.info("Set temperature to 0.01");
            }
        }
    }
}