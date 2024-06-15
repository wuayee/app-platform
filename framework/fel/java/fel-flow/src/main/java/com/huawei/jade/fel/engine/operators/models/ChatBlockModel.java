/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.core.model.BlockModel;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 阻塞对话模型实现。
 *
 * @author 刘信宏
 * @since 2024-04-16
 */
public class ChatBlockModel implements BlockModel<Prompt, ChatMessage> {
    private final ChatModelService provider;
    private final ChatOptions options;
    private final Map<String, Object> args;

    public ChatBlockModel(ChatModelService provider) {
        this(provider, new ChatOptions());
    }

    public ChatBlockModel(ChatModelService provider, ChatOptions options) {
        this(provider, options, Collections.emptyMap());
    }

    private ChatBlockModel(ChatModelService provider, ChatOptions options, Map<String, Object> args) {
        this.provider = Validation.notNull(provider, "The model provider cannot be null.");
        this.options = Validation.notNull(options, "The chat options cannot be null.");
        this.args = Validation.notNull(args, "The model args cannot be null.");
    }

    @Override
    public ChatMessage invoke(Prompt input) {
        Validation.notNull(input, "The model input data cannot be null.");
        ChatCompletion completionRequest = new ChatCompletion(input, this.options);
        ChatMessage message = this.provider.generate(completionRequest);
        Validation.notNull(message, "The model chat message can not be null.");
        Validation.equals(message.type(), MessageType.AI, "The message type must be {0}. [actualMessageType={1}]",
                MessageType.AI, message.type());

        AiMessage answer = new AiMessage(message.text(), message.toolCalls());
        this.updateMemory(answer);
        return answer;
    }

    @Override
    public ChatBlockModel bind(Map<String, Object> args) {
        ChatOptions dynamicOptions = this.getDynamicChatOptions(args);
        return new ChatBlockModel(this.provider, dynamicOptions, args);
    }

    /**
     * 绑定模型超参数。
     *
     * @param options 表示模型超参数的 {@link ChatOptions}。
     * @return 表示绑定了超参数的 {@link ChatBlockModel}。
     * @throws IllegalArgumentException 当 {@code options} 为 {@code null} 时。
     */
    public ChatBlockModel bind(ChatOptions options) {
        Validation.notNull(options, "The chat options cannot be null.");
        return new ChatBlockModel(this.provider, options);
    }

    private void updateMemory(AiMessage answer) {
        FlowSession session = cast(this.args.get(StateKey.FLOW_SESSION));
        if (session == null) {
            return;
        }
        Memory memory = session.getInnerState(StateKey.HISTORY_OBJ);
        if (answer.isToolCall() || memory == null) {
            return;
        }
        memory.add(session.getInnerState(StateKey.HISTORY_INPUT), answer);
    }

    private ChatOptions getDynamicChatOptions(Map<String, Object> dynamicArgs) {
        FlowSession session = cast(dynamicArgs.get(StateKey.FLOW_SESSION));
        if (session == null) {
            return this.options;
        }
        return Optional.ofNullable(session.<ChatOptions>getInnerState(StateKey.CHAT_OPTIONS))
                .orElse(this.options);
    }
}
