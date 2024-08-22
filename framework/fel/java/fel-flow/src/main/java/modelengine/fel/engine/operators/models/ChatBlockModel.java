/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.models;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import modelengine.fitframework.inspection.Validation;
import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatModelService;
import modelengine.fel.chat.ChatOptions;
import modelengine.fel.chat.MessageType;
import modelengine.fel.chat.Prompt;
import modelengine.fel.chat.character.AiMessage;
import modelengine.fel.chat.protocol.ChatCompletion;
import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.model.BlockModel;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fel.engine.util.StateKey;

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

    public ChatBlockModel(ChatModelService provider) {
        this(provider, new ChatOptions());
    }

    public ChatBlockModel(ChatModelService provider, ChatOptions options) {
        this.provider = Validation.notNull(provider, "The model provider cannot be null.");
        this.options = Validation.notNull(options, "The chat options cannot be null.");
    }

    @Override
    public ChatMessage invoke(Prompt input) {
        Validation.notNull(input, "The model input data cannot be null.");
        ChatOptions dynamicOptions = AiFlowSession.get()
                .map(session -> session.<ChatOptions>getInnerState(StateKey.CHAT_OPTIONS))
                .orElse(this.options);

        ChatCompletion completionRequest = new ChatCompletion(input, dynamicOptions);
        ChatMessage message = this.provider.generate(completionRequest);
        Validation.notNull(message, "The model chat message can not be null.");
        Validation.equals(message.type(), MessageType.AI, "The message type must be {0}. [actualMessageType={1}]",
                MessageType.AI, message.type());

        AiMessage answer = new AiMessage(message.text(), message.toolCalls());
        this.updateMemory(answer);
        return answer;
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
        if (answer.isToolCall()) {
            return;
        }
        Optional<FlowSession> session = AiFlowSession.get();
        if (!session.isPresent()) {
            return;
        }
        Memory memory = session.get().getInnerState(StateKey.HISTORY_OBJ);
        if (memory == null) {
            return;
        }
        memory.add(session.get().getInnerState(StateKey.HISTORY_INPUT), answer);
    }
}
