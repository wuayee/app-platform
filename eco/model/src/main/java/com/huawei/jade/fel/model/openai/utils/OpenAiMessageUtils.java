/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.utils;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.resource.UrlUtils;
import com.huawei.fitframework.resource.web.Media;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.message.Role;
import com.huawei.jade.fel.model.openai.entity.chat.message.content.UserContent;
import com.huawei.jade.fel.model.openai.entity.chat.message.tool.OpenAiTool;
import com.huawei.jade.fel.model.openai.entity.chat.message.tool.OpenAiToolCall;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理 OpenAI 消息的工具类。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024/4/30
 */
public class OpenAiMessageUtils {
    /**
     * 用于处理 OpenAI 对象序列化及反序列化。
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static UserContent buildUserContent(Media media) {
        String data = UrlUtils.isUrl(media.getData())
                ? media.getData()
                : String.format("data:%s;base64,%s", media.getMime(), media.getData());
        return UserContent.image(data);
    }

    private static OpenAiToolCall buildOpenaiToolCall(ToolCall toolCall) {
        return OpenAiToolCall.build(toolCall.getId(), toolCall.getName(), toolCall.getParameters());
    }

    private static Object getMessageContent(FlatChatMessage message) {
        Object content;

        List<Media> medias = message.medias();
        if (CollectionUtils.isNotEmpty(medias)) {
            List<UserContent> userContents = medias
                    .stream()
                    .map(OpenAiMessageUtils::buildUserContent)
                    .collect(Collectors.toList());
            userContents.add(UserContent.text(message.text()));
            content = userContents;
        } else {
            content = message.getText();
        }

        return content;
    }

    private static OpenAiChatMessage buildMessage(FlatChatMessage inputMessage) {
        List<OpenAiToolCall> toolCalls = null;
        if (CollectionUtils.isNotEmpty(inputMessage.getToolCalls())) {
            toolCalls = inputMessage.getToolCalls()
                    .stream()
                    .map(OpenAiMessageUtils::buildOpenaiToolCall)
                    .collect(Collectors.toList());
        }

        String toolCallId = null;
        if (StringUtils.isNotBlank(inputMessage.getId())) {
            toolCallId = inputMessage.getId();
        }
        Object content = getMessageContent(inputMessage);

        return new OpenAiChatMessage(
                Role.generateRole(inputMessage.type()),
                content,
                toolCallId,
                toolCalls);
    }

    /**
     * 将一组 {@link FlatChatMessage} 转化为一组 {@link OpenAiChatMessage} 。
     *
     * @param input {@link FlatChatMessage} 列表。
     * @return {@link OpenAiChatMessage} 列表。
     */
    public static List<OpenAiChatMessage> buildPrompts(List<FlatChatMessage> input) {
        if (input == null) {
            return Collections.emptyList();
        }
        return input.stream().map(OpenAiMessageUtils::buildMessage).collect(Collectors.toList());
    }

    /**
     * 将一组 {@link Tool} 转化为一组 {@link OpenAiTool} 。
     *
     * @param input {@link Tool}列表。
     * @return {@link OpenAiTool}列表。
     */
    public static List<OpenAiTool> buildTools(List<Tool> input) {
        if (input == null) {
            return Collections.emptyList();
        }

        List<OpenAiTool> tools = new ArrayList<>();
        for (Tool t : input) {
            OpenAiTool tool = new OpenAiTool();
            Map<String, Object> schema = t.getSchema();
            OpenAiTool.Function func = new OpenAiTool.Function();

            if (schema == null) {
                continue;
            }

            tool.setType("function");
            if (schema.get("name") instanceof String) {
                func.setName((String) schema.get("name"));
            }
            if (schema.getOrDefault("description", "") instanceof String) {
                func.setDescription((String) schema.getOrDefault("description", ""));
            }
            func.setParameters((Map<String, Object>) schema.get("parameters"));
            tool.setFunction(func);
            tools.add(tool);
        }

        return tools;
    }

    /**
     * 将 FEL 格式的请求转化为 OpenAI 格式的文本补全请求。
     *
     * @param request FEL 格式的请求。
     * @return OpenAI 格式的文本补全请求。
     */
    public static OpenAiChatCompletionRequest buildChatCompletionRequest(ChatCompletion request) {
        Validation.notNull(request, "Failed to generate OpenAiChatCompletionRequest: request is null.");
        ChatOptions options = ObjectUtils.getIfNull(request.getOptions(), ChatOptions::new);
        String model = Validation.notBlank(options.getModel(), "The model name in request is empty.");

        List<OpenAiChatMessage> messages = OpenAiMessageUtils.buildPrompts(request.getMessages());
        List<OpenAiTool> tools = OpenAiMessageUtils.buildTools(options.getTools());
        return OpenAiChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .frequencyPenalty(options.getFrequencyPenalty())
                .maxTokens(options.getMaxTokens())
                .presencePenalty(options.getPresencePenalty())
                .stop(options.getStop())
                .temperature(options.getTemperature())
                .tools(tools)
                .toolChoice(CollectionUtils.isEmpty(tools) ? null : "auto")
                .apiKey(options.getApiKey())
                .build();
    }

    /**
     * 将 OpenAI 格式的模型响应转化为 FEL 格式的人工智能消息。
     *
     * @param response OpenAI 格式的模型响应。
     * @return FEL 格式的人工智能消息。
     */
    public static FlatChatMessage buildFelAiMessage(OpenAiChatCompletionResponse response) {
        FlatChatMessage emptyMessage = FlatChatMessage.from(new AiMessage(""));
        if (response == null || CollectionUtils.isEmpty(response.getChoices())) {
            return emptyMessage;
        }

        OpenAiChatMessage openAiChatMessage = response.getChoices().get(0).getMessage();
        if (openAiChatMessage == null) {
            return emptyMessage;
        }

        List<ToolCall> toolCalls = openAiChatMessage.getToolCalls()
                .stream()
                .map(OpenAiToolCall::buildFelToolCall)
                .collect(Collectors.toList());

        String text = "";
        if (openAiChatMessage.getContent() instanceof String) {
            text = (String) openAiChatMessage.getContent();
        }

        return FlatChatMessage.from(new AiMessage(text, toolCalls));
    }
}
