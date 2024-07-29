/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.model.openai.client.OpenAiClient;
import com.huawei.jade.fel.model.openai.entity.chat.OpenAiChatCompletionRequest;
import com.huawei.jade.fel.model.openai.utils.OpenAiMessageUtils;

import java.io.IOException;

/**
 * FEL 模型同步接口的 OpenAI 实现。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Component
public class OpenAiChatModelService implements ChatModelService {
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
        return generateResponseMessage(OpenAiMessageUtils.buildChatCompletionRequest(request));
    }

    private FlatChatMessage generateResponseMessage(OpenAiChatCompletionRequest request) {
        try {
            return OpenAiMessageUtils.buildFelAiMessage(this.openAiClient.createChatCompletion(request));
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
