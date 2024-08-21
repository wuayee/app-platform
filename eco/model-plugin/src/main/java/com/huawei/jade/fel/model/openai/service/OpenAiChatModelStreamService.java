/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.service;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.flowable.Choir;
import com.huawei.jade.fel.chat.ChatModelStreamService;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.model.openai.client.OpenAiClientSse;
import com.huawei.jade.fel.model.openai.utils.OpenAiMessageUtils;

/**
 * FEL 模型流式接口的 OpenAI 实现。
 *
 * @author 张庭怿
 * @since 2024-5-16
 */
@Component
public class OpenAiChatModelStreamService implements ChatModelStreamService {
    private OpenAiClientSse openAiClient;

    public OpenAiChatModelStreamService(OpenAiClientSse openAiClient) {
        this.openAiClient = openAiClient;
    }

    /**
     * 将 FEL 输入转换为 OpenAI API 格式的会话补全请求发送给大模型，并将大模型流式响应转换为 FEL 格式的消息。
     *
     * @param request 表示聊天请求的 {@link ChatCompletion} 。
     * @return 模型流式响应。
     */
    @Override
    @Fitable(id = "com.huawei.fit.jade.model.client.openai.chat.stream.generate")
    public Choir<FlatChatMessage> generate(ChatCompletion request) {
        return this.openAiClient.generate(OpenAiMessageUtils.buildChatCompletionRequest(request));
    }
}
