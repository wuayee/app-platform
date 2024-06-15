/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitter;
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
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 流式对话模型实现。
 *
 * @author 刘信宏
 * @since 2024-05-16
 */
public class ChatStreamModel implements StreamModel<Prompt, ChatMessage> {
    private final ChatModelStreamService provider;
    private final ChatOptions options;
    private final Map<String, Object> args;

    public ChatStreamModel(ChatModelStreamService provider) {
        this(provider, new ChatOptions());
    }

    public ChatStreamModel(ChatModelStreamService provider, ChatOptions options) {
        this(provider, options, Collections.emptyMap());
    }

    private ChatStreamModel(ChatModelStreamService provider, ChatOptions options, Map<String, Object> args) {
        this.provider = Validation.notNull(provider, "The model provider can not be null.");
        this.options = Validation.notNull(options, "The chat options can not be null.");
        this.args = Validation.notNull(args, "The model args cannot be null.");
    }

    @Override
    public ChatStreamModel bind(Map<String, Object> args) {
        ChatOptions dynamicOptions = this.getDynamicChatOptions(args);
        return new ChatStreamModel(this.provider, dynamicOptions, args);
    }

    /**
     * 绑定模型超参数。
     *
     * @param options 表示模型超参数的 {@link ChatOptions}。
     * @return 表示绑定了超参数的 {@link ChatBlockModel}。
     * @throws IllegalArgumentException 当 {@code options} 为 {@code null} 时。
     */
    public ChatStreamModel bind(ChatOptions options) {
        Validation.notNull(options, "The chat options cannot be null.");
        return new ChatStreamModel(this.provider, options);
    }

    @Override
    public FiniteEmitter<ChatMessage, ChatChunk> invoke(Prompt input) {
        Validation.notNull(input, "The model input data can not be null.");

        ChatCompletion completionRequest = new ChatCompletion(input, this.options);
        Choir<ChatMessage> choir = ObjectUtils.cast(this.provider.generate(completionRequest));
        return new LlmEmitter<>(choir, getEmitterDataBuilder(), this.args, input);
    }

    private ChatOptions getDynamicChatOptions(Map<String, Object> dynamicArgs) {
        FlowSession session = cast(dynamicArgs.get(StateKey.FLOW_SESSION));
        if (session == null) {
            return this.options;
        }
        return Optional.ofNullable(session.<ChatOptions>getInnerState(StateKey.CHAT_OPTIONS))
                .orElse(this.options);
    }

    private static FiniteEmitterDataBuilder<ChatMessage, ChatChunk> getEmitterDataBuilder() {
        return new FiniteEmitterDataBuilder<ChatMessage,
                ChatChunk>() {
            @Override
            public ChatChunk data(ChatMessage data) {
                Validation.notNull(data, "The chat message can not be null.");
                return new ChatChunk(data.text(), data.medias(), data.toolCalls());
            }

            @Override
            public ChatChunk end() {
                return new ChatChunk().setEnd();
            }

            @Override
            public ChatChunk error(String message) {
                return new ChatChunk(new Exception(Optional.ofNullable(message).orElse(StringUtils.EMPTY)));
            }
        };
    }
}
