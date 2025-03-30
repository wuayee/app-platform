/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.engine.operators.models;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fel.engine.util.StateKey;
import modelengine.fit.waterflow.bridge.fitflow.FiniteEmitter;
import modelengine.fit.waterflow.bridge.fitflow.FiniteEmitterDataBuilder;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 流式对话模型实现。
 *
 * @author 刘信宏
 * @since 2024-05-16
 */
public class ChatStreamModel implements StreamModel<Prompt, ChatMessage> {
    private static final Integer MAXIMUM_POOL_SIZE = 50;
    private static final ExecutorService THREAD_POOL =
            new ThreadPoolExecutor(0, MAXIMUM_POOL_SIZE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    private final ChatModel provider;
    private final ChatOption option;

    public ChatStreamModel(ChatModel provider) {
        this(provider, ChatOption.custom().build());
    }

    public ChatStreamModel(ChatModel provider, ChatOption option) {
        this.provider = Validation.notNull(provider, "The model provider can not be null.");
        this.option = Validation.notNull(option, "The chat options can not be null.");
    }

    /**
     * 绑定模型超参数。
     *
     * @param option 表示模型超参数的 {@link ChatOption}。
     * @return 表示绑定了超参数的 {@link ChatStreamModel}。
     * @throws IllegalArgumentException 当 {@code options} 为 {@code null} 时。
     */
    public ChatStreamModel bind(ChatOption option) {
        Validation.notNull(option, "The chat options cannot be null.");
        return new ChatStreamModel(this.provider, option);
    }

    @Override
    public FiniteEmitter<ChatMessage, ChatChunk> invoke(Prompt input) {
        Validation.notNull(input, "The model input data can not be null.");
        FlowSession flowSession = AiFlowSession.get()
                .orElseThrow(() -> new IllegalStateException("The ai session cannot be empty."));
        ChatOption dynamicOptions =
                Optional.ofNullable(flowSession.<ChatOption>getInnerState(StateKey.CHAT_OPTIONS)).orElse(this.option);
        ChatOption chatOption = ChatOption.custom()
                .model(dynamicOptions.model())
                .stream(true)
                .apiKey(dynamicOptions.apiKey())
                .baseUrl(dynamicOptions.baseUrl())
                .secureConfig(dynamicOptions.secureConfig())
                .frequencyPenalty(dynamicOptions.frequencyPenalty())
                .maxTokens(dynamicOptions.maxTokens())
                .presencePenalty(dynamicOptions.presencePenalty())
                .temperature(dynamicOptions.temperature())
                .topP(dynamicOptions.topP())
                .stop(dynamicOptions.stop())
                .tools(dynamicOptions.tools())
                .build();
        Choir<ChatMessage> choir = this.provider.generate(input, chatOption);
        Choir<ChatMessage> asyncChoir = Choir.create(emitter -> {
            THREAD_POOL.execute(() -> {
                choir.subscribe(null,
                        (subscription, chatMessage) -> emitter.emit(chatMessage),
                        subscription -> emitter.complete(),
                        (subscription, exception) -> emitter.fail(exception));
            });
        });
        return new LlmEmitter<>(asyncChoir, getEmitterDataBuilder(), input, flowSession);
    }

    private static FiniteEmitterDataBuilder<ChatMessage, ChatChunk> getEmitterDataBuilder() {
        return new FiniteEmitterDataBuilder<ChatMessage, ChatChunk>() {
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
