/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.entity.chat;

import modelengine.fel.model.openai.entity.chat.message.OpenAiChatMessage;
import modelengine.fel.model.openai.entity.chat.message.tool.OpenAiTool;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

/**
 * OpenAi API 格式的会话补全请求。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class OpenAiChatCompletionRequest {
    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-messages">OpenAI API</a>
     */
    @NonNull
    private List<OpenAiChatMessage> messages;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-model">OpenAI API</a>
     */
    @NonNull
    private String model;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-frequency_penalty">
     * OpenAI API</a>
     */
    @Property(name = "frequency_penalty")
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-max_tokens">OpenAI API</a>
     */
    @Property(name = "max_tokens")
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-presence_penalty">
     * OpenAI API</a>
     */
    @Property(name = "presence_penalty")
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-stop">OpenAI API</a>
     */
    private List<String> stop;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-stream">OpenAI API</a>
     */
    @Setter
    private boolean stream;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-temperature">OpenAI API</a>
     */
    private Double temperature;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-top_p">OpenAI API</a>
     */
    @Property(name = "top_p")
    @JsonProperty("top_p")
    private Double topP;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-tools">OpenAI API</a>
     */
    @Property(name = "tools")
    @JsonProperty("tools")
    private List<OpenAiTool> tools;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/chat/create#chat-create-tool_choice">OpenAI API</a>
     */
    @Property(name = "tool_choice")
    @JsonProperty("tool_choice")
    private String toolChoice;

    /**
     * @see <a href="https://platform.openai.com/docs/api-reference/authentication">OpenAI API</a>
     */
    @JsonIgnore
    private String apiKey;
}
