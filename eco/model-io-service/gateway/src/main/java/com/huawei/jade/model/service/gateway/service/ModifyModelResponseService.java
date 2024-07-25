/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.service;

import com.huawei.jade.model.service.gateway.entity.ChatMessage;
import com.huawei.jade.model.service.gateway.entity.FunctionCallModel;
import com.huawei.jade.model.service.gateway.utils.ModelMessageUtilis;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 用于修改大模型返回的回应,添加tool_calls字段。
 *
 * @author 王浩冉
 * @since 2024-07-16
 */
@Service
@Slf4j
public class ModifyModelResponseService implements RewriteFunction<String, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        if (!"true".equals(System.getenv("SUPPORT_TOOLS"))) {
            log.warn("Skip modify response.");
            return Mono.just(body);
        }

        if (isStreamResponse(body)) {
            String head = body.substring(0, 6);
            String[] data = body.replace(head, "").split("\\n+");
            if (!isQwenModel(data[0])) {
                log.info("Skip modify response");
                return Mono.just(body);
            }
            return Mono.justOrEmpty(processStreamResponse(body, data));
        }

        if (!isQwenModel(body)) {
            log.info("Skip modify response");
            return Mono.just(body);
        }
        return Mono.justOrEmpty(processSyncResponse(body));
    }

    // 非流式处理方式
    private String processSyncResponse(String body) {
        try {
            Map<String, Object> responseBody = OBJECT_MAPPER.readValue(body, Map.class);

            List<Map<String, Object>> choices = new ArrayList<>();
            if (responseBody.get("choices") instanceof ArrayList) {
                choices = (List<Map<String, Object>>) responseBody.get("choices");
            }
            if (choices.isEmpty()) {
                log.info("Choices is empty");
                return body;
            }
            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = null;
            if (choice.get("message") instanceof Map) {
                message = (Map<String, Object>) choice.get("message");
            }

            ChatMessage chatMessage = null;
            if (message != null && message.get("role") instanceof String && message.get("content") instanceof String) {
                chatMessage = new ChatMessage((String) message.get("role"),
                        Optional.ofNullable((String) message.get("content")), null, null, null, null);
            }
            ChatMessage resultMessage = null;
            if (chatMessage != null) {
                FunctionCallModel fncall = new FunctionCallModel();
                resultMessage = ModelMessageUtilis.postprocessFncallMessages(fncall, chatMessage);
            }

            if (resultMessage != null && resultMessage.getToolCalls() != null
                    && resultMessage.getToolCalls().isPresent()) {
                message.put("tool_calls", resultMessage.getToolCalls().get());
                message.put("content", "");
                log.info("Update response add tool_calls");
            }

            return OBJECT_MAPPER.writeValueAsString(responseBody);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to modify model request:" + e + "Responsebody:" + body);
        }
    }

    // 流式处理方式
    private String processStreamResponse(String body, String[] data) {
        try {
            String role = "assistant";
            StringBuilder content = new StringBuilder();
            getContent(data, content);

            ChatMessage messge = new ChatMessage(role, Optional.of(content.toString()), null, null, null, null);
            ChatMessage resultMessage = ModelMessageUtilis.postprocessFncallMessages(new FunctionCallModel(), messge);

            if (resultMessage.getToolCalls() == null || !resultMessage.getToolCalls().isPresent()) {
                log.warn("Skip modify response.");
                return body;
            }

            Map<String, Object> responseBody = OBJECT_MAPPER.readValue(data[data.length - 2], Map.class);
            String replacement = getReplacement(responseBody, resultMessage);
            String newBody = body;
            if (!replacement.isEmpty()) {
                newBody = "data: " + replacement + System.lineSeparator() + System.lineSeparator() + "data: "
                        + data[data.length - 1] + System.lineSeparator();
                log.info("Update response add tool_calls");
            }
            return newBody;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to modify model request:" + e + "Responsebody:" + body);
        }
    }

    private boolean isQwenModel(String body) {
        try {
            Map<String, Object> responseBody = OBJECT_MAPPER.readValue(body, Map.class);
            if (responseBody.get("model") == null) {
                log.info("model is null");
                return false;
            }
            return responseBody.get("model").toString().toLowerCase(Locale.ROOT).contains("qwen");
        } catch (IOException e) {
            throw new IllegalStateException("Failed to modify model request:" + e + "Responsebody:" + body);
        }
    }

    private boolean isStreamResponse(String body) {
        String judge = body.toLowerCase(Locale.ROOT);
        if (judge.startsWith("data: ")) {
            String[] data = body.replace("data: ", "").split("\\n+");
            if (data.length < 2 || data[0].equals("[done]")) {
                log.warn("Skip modify response.");
            } else {
                return true;
            }
        }
        return false;
    }

    private void getContent(String[] data, StringBuilder content) {
        try {
            for (int i = 0; i < data.length - 1; i++) {
                Map<String, Object> responseBody = OBJECT_MAPPER.readValue(data[i], Map.class);
                List<Map<String, Object>> choices = new ArrayList<>();
                if (responseBody.get("choices") instanceof ArrayList) {
                    choices = (List<Map<String, Object>>) responseBody.get("choices");
                }
                if (choices.isEmpty()) {
                    continue;
                }
                Map<String, Object> choice = choices.get(0);
                if (choice.get("delta") instanceof Map) {
                    Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
                    content.append(delta.get("content"));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to modify model request:" + e);
        }
    }

    private String getReplacement(Map<String, Object> responseBody, ChatMessage resultMessage) {
        try {
            List<Map<String, Object>> choices = new ArrayList<>();
            if (responseBody.get("choices") instanceof ArrayList) {
                choices = (List<Map<String, Object>>) responseBody.get("choices");
            }
            if (choices.isEmpty()) {
                return "";
            }
            Map<String, Object> choice = choices.get(0);
            if (choice.get("delta") instanceof Map) {
                Map<String, Object> delta = (Map<String, Object>) choice.get("delta");
                delta.putIfAbsent("role", "assistant");
                delta.putIfAbsent("content", resultMessage.getContent().orElse(""));
                if (resultMessage.getToolCalls() != null && resultMessage.getToolCalls().isPresent()) {
                    delta.put("tool_calls", resultMessage.getToolCalls().get());
                }
                return OBJECT_MAPPER.writeValueAsString(responseBody);
            }
            return "";
        } catch (IOException e) {
            throw new IllegalStateException("Failed to modify model request:" + e);
        }
    }
}