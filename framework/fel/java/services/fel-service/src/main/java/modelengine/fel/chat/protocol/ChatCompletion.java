/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.chat.protocol;

import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatOptions;
import modelengine.fel.chat.Prompt;
import modelengine.fitframework.inspection.Validation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示聊天模型请求实体类。
 *
 * @author 易文渊
 * @since 2024-04-12
 */
@Data
@NoArgsConstructor
public class ChatCompletion implements Prompt {
    /**
     * 会话消息列表。
     */
    private List<FlatChatMessage> messages;

    /**
     * 模型参数。
     */
    private ChatOptions options;

    /**
     * 根据 {@link Prompt} 实现类构建 {@link ChatCompletion} 实例。
     *
     * @param prompt 表示大模型输入的 {@link Prompt}。
     * @param options 表示大模型超参数的 {@link ChatOptions}。
     */
    public ChatCompletion(Prompt prompt, ChatOptions options) {
        Validation.notNull(prompt, "The prompt cannot be null.");
        Validation.notEmpty(prompt.messages(), "The messages cannot be empty");
        this.messages = prompt.messages().stream().map(FlatChatMessage::from).collect(Collectors.toList());
        this.options = Validation.notNull(options, "The option must not be null");
    }

    @Override
    public List<? extends ChatMessage> messages() {
        return this.messages;
    }
}