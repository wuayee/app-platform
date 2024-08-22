/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.models;

import modelengine.fit.waterflow.bridge.fitflow.FiniteEmitter;
import modelengine.fit.waterflow.bridge.fitflow.FiniteEmitterDataBuilder;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatModelStreamService;
import modelengine.fel.chat.ChatOptions;
import modelengine.fel.chat.Prompt;
import modelengine.fel.chat.protocol.ChatCompletion;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fel.engine.util.StateKey;

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

    public ChatStreamModel(ChatModelStreamService provider) {
        this(provider, new ChatOptions());
    }

    public ChatStreamModel(ChatModelStreamService provider, ChatOptions options) {
        this.provider = Validation.notNull(provider, "The model provider can not be null.");
        this.options = Validation.notNull(options, "The chat options can not be null.");
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
        FlowSession session = AiFlowSession.get()
                .orElseThrow(() -> new IllegalStateException("The ai session cannot be empty."));

        ChatOptions dynamicOptions = Optional.ofNullable(session.<ChatOptions>getInnerState(StateKey.CHAT_OPTIONS))
                .orElse(this.options);
        ChatCompletion completionRequest = new ChatCompletion(input, dynamicOptions);
        Choir<ChatMessage> choir = ObjectUtils.cast(this.provider.generate(completionRequest));
        return new LlmEmitter<>(choir, getEmitterDataBuilder(), input, session);
    }

    private static FiniteEmitterDataBuilder<ChatMessage, ChatChunk> getEmitterDataBuilder() {
        return new FiniteEmitterDataBuilder<ChatMessage,
                ChatChunk>() {
            @Override
            public ChatChunk data(ChatMessage data) {
                Validation.notNull(data, "The chat message can not be null.");
                return new ChatChunk(data.text(), data.toolCalls());
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
