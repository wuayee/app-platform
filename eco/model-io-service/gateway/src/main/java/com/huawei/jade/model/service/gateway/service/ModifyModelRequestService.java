/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.model.service.gateway.service;

import com.huawei.jade.model.service.gateway.entity.ChatMessage;
import com.huawei.jade.model.service.gateway.entity.FunctionCall;
import com.huawei.jade.model.service.gateway.entity.FunctionCallModel;
import com.huawei.jade.model.service.gateway.entity.FunctionDefinition;
import com.huawei.jade.model.service.gateway.entity.ToolCall;
import com.huawei.jade.model.service.gateway.entity.ToolCallResponse;
import com.huawei.jade.model.service.gateway.utils.ModelMessageUtilis;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

            if ("true".equals(System.getenv("SUPPORT_TOOLS"))) {
                if (isQwenModel(requestBody)) {
                    modifyTools(requestBody);
                } else {
                    log.info("Skip modify request for non-qwen model");
                }
            }

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

        if (!requestBody.containsKey("max_tokens") && modelName != null && this.maxTokens.containsKey(modelName)
                && this.maxTokens.get(modelName) != null) {
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

    private boolean isQwenModel(Map<String, Object> responseBody) {
        if (responseBody.get("model") == null) {
            log.info("Model is null");
            return false;
        }
        return responseBody.get("model").toString().toLowerCase(Locale.ROOT).contains("qwen");
    }

    private void modifyTools(Map<String, Object> requestBody) {
        if (requestBody.get("tools") == null) {
            log.warn("Tools is null");
            return;
        }

        List<ChatMessage> messages = new ArrayList<>(getMessages(requestBody));

        List<ToolCall> tools = new ArrayList<>(getTools(requestBody));
        requestBody.remove("tools");
        requestBody.remove("tool_choice");

        FunctionCallModel fncall = new FunctionCallModel();
        List<ChatMessage> prependMessages = ModelMessageUtilis.prependToolsSystem(fncall, messages, tools);
        List<ChatMessage> preprocessMessages = ModelMessageUtilis.preprocessFncallMessages(fncall, prependMessages);
        List<Map<String, String>> messagesMap = new ArrayList<>();
        for (ChatMessage message : preprocessMessages) {
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("role", message.getRole());
            messageMap.put("content", message.getContent().orElse(null));
            messagesMap.add(messageMap);
        }
        requestBody.put("messages", messagesMap);

        log.info("RequestBody modified remove tools and tool_choice");
    }

    private List<ChatMessage> getMessages(Map<String, Object> requestBody) {
        List<ChatMessage> messages = new ArrayList<>();
        List<Map<String, String>> messagesMap = new ArrayList<>();
        if (requestBody.get("messages") instanceof ArrayList) {
            messagesMap = (List<Map<String, String>>) requestBody.get("messages");
        }

        if (messagesMap.isEmpty()) {
            return messages;
        }

        for (Map<String, String> messageMap : messagesMap) {
            List<ToolCallResponse> toolCalls = null;
            if (messageMap.get("tool_calls") != null) {
                toolCalls = new ArrayList<>();
                List<Map<String, Object>> toolCallsMap = (List<Map<String, Object>>) requestBody.get("tool_calls");
                for (Map<String, Object> toolCallMap : toolCallsMap) {
                    Map<String, Object> functionMap = (Map<String, Object>) toolCallMap.get("function");
                    ToolCallResponse toolCall = new ToolCallResponse(toolCallMap.get("id").toString(),
                            toolCallMap.get("type").toString(),
                            new FunctionCall(functionMap.get("name").toString(), functionMap.get("args").toString()));
                    toolCalls.add(toolCall);
                }
            }
            ChatMessage message = new ChatMessage(messageMap.get("role"),
                    Optional.ofNullable(messageMap.get("content")), null, null,
                    Optional.ofNullable(messageMap.get("tool_choice")), Optional.ofNullable(toolCalls));
            messages.add(message);
        }

        return messages;
    }

    private List<ToolCall> getTools(Map<String, Object> requestBody) {
        List<ToolCall> tools = new ArrayList<>();
        if (requestBody.get("tools") instanceof ArrayList) {
            List<Map<String, Object>> toolsMap = (List<Map<String, Object>>) requestBody.get("tools");
            for (Map<String, Object> toolMap : toolsMap) {
                Map<String, Object> functionMap = null;
                String functionName = "";
                String functionDescription = "";

                if (toolMap.get("type").equals("function")) {
                    functionMap = (Map<String, Object>) toolMap.get("function");
                }

                if (functionMap == null) {
                    continue;
                }

                if (functionMap.get("name") instanceof String) {
                    functionName = (String) functionMap.get("name");
                }

                if (functionMap.get("description") instanceof String) {
                    functionDescription = (String) functionMap.get("description");
                }

                Map<String, Object> parametersMap = (Map<String, Object>) functionMap.get("parameters");
                ToolCall toolCall = new ToolCall("function",
                        new FunctionDefinition(functionName, functionDescription, Optional.of(parametersMap)));
                tools.add(toolCall);
            }
        }
        return tools;
    }
}