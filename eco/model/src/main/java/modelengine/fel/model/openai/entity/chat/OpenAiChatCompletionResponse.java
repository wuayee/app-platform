/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.entity.chat;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.character.AiMessage;
import modelengine.fel.model.openai.entity.Usage;
import modelengine.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import lombok.Data;

import java.util.List;

/**
 * OpenAi API 格式的会话补全响应。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiChatCompletionResponse {
    private static final ChatMessage EMPTY_RESPONSE = new AiMessage(StringUtils.EMPTY);

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-choices">OpenAI API</a>
     */
    private List<OpenAiChatCompletionChoice> choices;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/object#chat/object-usage">OpenAI API</a>
     */
    private Usage usage;

    /**
     * 获取响应中的消息。
     *
     * @return 表示模型回复的 {@link ChatMessage}。
     */
    public ChatMessage message() {
        if (CollectionUtils.isEmpty(choices)) {
            return EMPTY_RESPONSE;
        }
        OpenAiChatMessage openAiChatMessage = choices.get(0).getMessage();
        if (openAiChatMessage == null) {
            return EMPTY_RESPONSE;
        }
        String content = StringUtils.EMPTY;
        if (openAiChatMessage.getContent() instanceof String) {
            content = cast(openAiChatMessage.getContent());
        }
        return new AiMessage(content, openAiChatMessage.toolCalls());
    }
}
