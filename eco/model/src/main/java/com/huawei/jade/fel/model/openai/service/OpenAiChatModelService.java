/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionChoice;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import com.huawei.jade.fel.model.openai.entity.chat.message.tool.OpenAiTool;
import com.huawei.jade.fel.model.openai.entity.chat.message.tool.OpenAiToolCall;
import com.huawei.jade.fel.model.openai.utils.OpenAiMessageUtils;
import com.huawei.jade.fel.tool.ToolCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FEL 模型同步接口的 OpenAI 实现。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Component
public class OpenAiChatModelService implements ChatModelService {
    private static final Logger LOGGER = Logger.get(OpenAiChatModelService.class);

    private OpenAiClient openAiClient;

    public OpenAiChatModelService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    /**
     * 将 FEL 输入转换为 OpenAI API 格式的会话补全请求发送给大模型，并将大模型响应转换为 FEL 格式的消息。
     *
     * @param request 表示聊天请求的 {@link ChatCompletion} 。
     * @return 模型响应消息。
     */
    @Override
    @Fitable(id = "com.huawei.fit.jade.model.client.openai.chat.generate")
    public FlatChatMessage generate(ChatCompletion request) {
        OpenAiChatCompletionRequest r = buildRequest(request);
        return generateResponseMessage(r);
    }

    private OpenAiChatCompletionRequest buildRequest(ChatCompletion request) {
        if (request == null) {
            throw new IllegalArgumentException("Failed to generate chat message: request is null.");
        }

        ChatOptions options = ObjectUtils.getIfNull(request.getOptions(), ChatOptions::new);
        String model = StringUtils.blankIf(options.getModel(), "");
        if (StringUtils.isBlank(model)) {
            LOGGER.warn("Empty model name");
        }

        List<OpenAiChatMessage> messages = OpenAiMessageUtils.buildPrompts(request.getMessages());
        List<OpenAiTool> tools = OpenAiMessageUtils.buildTools(options.getTools());
        return OpenAiChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .frequencyPenalty(options.getFrequencyPenalty())
                .maxTokens(options.getMaxTokens())
                .presencePenalty(options.getPresencePenalty())
                .stop(options.getStop())
                .stream(false) // generate是同步接口
                .temperature(options.getTemperature())
                .tools(tools)
                .toolChoice(CollectionUtils.isEmpty(tools) ? null : "auto")
                .build();
    }

    private FlatChatMessage generateResponseMessage(OpenAiChatCompletionRequest request) {
        List<ToolCall> toolCalls = new ArrayList<>();
        String text = "";

        try {
            List<OpenAiChatCompletionChoice> choices = this.openAiClient.createChatCompletion(request).getChoices();
            if (choices == null || choices.isEmpty()) {
                return new FlatChatMessage(new AiMessage("", null));
            }

            OpenAiChatMessage message = choices.get(0).getMessage();
            toolCalls = message.getToolCalls()
                    .stream()
                    .map(OpenAiToolCall::buildFelToolCall)
                    .collect(Collectors.toList());

            if (message.getContent() instanceof String) {
                text = (String) message.getContent();
            }
        } catch (IOException e) {
            text = e.toString();
        }

        return new FlatChatMessage(new AiMessage(text, toolCalls));
    }
}
