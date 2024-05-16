/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitter;
import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitterData;
import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitterDataBuilder;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelStreamService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.engine.operators.AiRunnableArg;
import com.huawei.jade.fel.engine.operators.CustomState;

import java.util.Optional;

/**
 * 流式对话模型实现。
 *
 * @param <M> 表示模型算子对应的流程节点的入参类型。
 * @author 刘信宏
 * @since 2024-05-16
 */
public class ChatStreamModel<M> implements ChatModel<FiniteEmitter<ChatMessage, ChatChunk>> {
    private final ChatModelStreamService provider;
    private final ChatOptions options;

    public ChatStreamModel(ChatModelStreamService provider) {
        this(provider, new ChatOptions());
    }

    private ChatStreamModel(ChatModelStreamService provider, ChatOptions options) {
        this.provider = Validation.notNull(provider, "Model provider cannot be null.");
        this.options = Validation.notNull(options, "ChatOptions cannot be null.");
    }

    @Override
    public FiniteEmitter<ChatMessage, ChatChunk> invoke(CustomState<Prompt> arg) {
        Validation.notNull(arg, "Runnable arg cannot be null.");
        AiRunnableArg<Prompt> aiArg = ObjectUtils.cast(arg);

        ChatCompletion completionRequest = new ChatCompletion(aiArg.data(), this.options);
        Choir<ChatMessage> choir = ObjectUtils.cast(provider.generate(completionRequest));
        FiniteEmitterDataBuilder<ChatMessage, ChatChunk> builder = getEmitterDataBuilder();
        return new LlmEmitter<>(choir, builder, aiArg);
    }

    @Override
    public ChatStreamModel<M> bind(ChatOptions options) {
        if (options == null) {
            return this;
        }
        return new ChatStreamModel<>(this.provider, options);
    }

    private static FiniteEmitterDataBuilder<ChatMessage, ChatChunk> getEmitterDataBuilder() {
        return new FiniteEmitterDataBuilder<ChatMessage,
                ChatChunk>() {
            @Override
            public ChatChunk data(ChatMessage data) {
                return new ChatChunk(data);
            }

            @Override
            public ChatChunk end() {
                return new ChatChunk(StringUtils.EMPTY).setEnd();
            }

            @Override
            public ChatChunk error(String message) {
                // todo: message 无调用栈
                return new ChatChunk(new Exception(Optional.ofNullable(message).orElse(StringUtils.EMPTY)));
            }
        };
    }
}
