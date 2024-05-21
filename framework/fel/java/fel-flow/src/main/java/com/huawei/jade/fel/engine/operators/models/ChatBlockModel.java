/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatModelService;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.ChatCompletion;
import com.huawei.jade.fel.engine.operators.AiRunnableArg;
import com.huawei.jade.fel.engine.operators.CustomState;
import com.huawei.jade.fel.engine.util.StateKey;

/**
 * 阻塞对话模型实现。
 *
 * @param <M> 表示模型算子对应的流程节点的入参类型。
 * @author 刘信宏
 * @since 2024-04-16
 */
public class ChatBlockModel<M> implements ChatModel<AiMessage> {
    private final ChatModelService provider;
    private final ChatOptions options;

    public ChatBlockModel(ChatModelService provider) {
        this(provider, new ChatOptions());
    }

    public ChatBlockModel(ChatModelService provider, ChatOptions options) {
        this.provider = Validation.notNull(provider, "Model provider cannot be null.");
        this.options = Validation.notNull(options, "ChatOptions cannot be null.");
    }

    @Override
    public AiMessage invoke(CustomState<Prompt> arg) {
        Validation.notNull(arg, "Runnable arg cannot be null.");
        AiRunnableArg<Prompt> aiArg = ObjectUtils.cast(arg);
        ChatCompletion completionRequest = new ChatCompletion(aiArg.data(), this.options);

        ChatMessage message = provider.generate(completionRequest);
        Validation.equals(message.type(), MessageType.AI, "message type must be ai");
        AiMessage answer = new AiMessage(message.text(), message.toolCalls());
        if (answer.isToolCall() || aiArg.memory() == null) {
            return answer;
        }
        // Try to get origin Q&A
        aiArg.memory().add(ObjectUtils.cast(aiArg.getInnerState(StateKey.HISTORY_INPUT)), answer);
        return answer;
    }

    @Override
    public ChatBlockModel<M> bind(ChatOptions options) {
        Validation.notNull(options, "ChatOptions cannot be null.");
        return new ChatBlockModel<>(provider, options);
    }
}
