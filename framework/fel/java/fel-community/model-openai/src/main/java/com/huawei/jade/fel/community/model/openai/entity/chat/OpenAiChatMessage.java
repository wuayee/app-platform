/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.model.openai.entity.chat;

import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.serialization.annotation.SerializeStrategy;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.fel.core.chat.ChatMessage;
import com.huawei.jade.fel.core.chat.MessageType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 此类用于表示 OpenAI 的消息对象。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class OpenAiChatMessage {
    private static final Map<MessageType, String> ROLE_MAP = MapBuilder.<MessageType, String>get()
            .put(MessageType.SYSTEM, "system")
            .put(MessageType.HUMAN, "user")
            .put(MessageType.AI, "assistant")
            .put(MessageType.TOOL, "tool")
            .build();

    private String role;

    /**
     * 使用 {@link Object} 对类型进行通用表示，有以下两种类型：
     * <ol>
     *     <li>{@link String} 类型，表示单纯文本消息；</li>
     *     <li>由 {@link UserContent} 组成的列表。</li>
     * </ol>
     */
    private Object content;

    @Property(name = "tool_call_id")
    private String toolCallId;

    @Property(name = "tool_calls")
    private List<OpenAiToolCall> toolCalls;

    /**
     * 将 {@link ChatMessage} 对象转换为 {@link OpenAiChatMessage} 对象。
     *
     * @param chatMessage 表示标准聊天消息的 {@link ChatMessage}。
     * @return 表示 openai 聊天消息的 {@link OpenAiChatMessage}。
     */
    public static OpenAiChatMessage from(ChatMessage chatMessage) {
        OpenAiChatMessage openAiChatMessage = new OpenAiChatMessage();
        openAiChatMessage.role = ROLE_MAP.get(chatMessage.type());
        if (CollectionUtils.isNotEmpty(chatMessage.medias())) {
            List<UserContent> userContents =
                    chatMessage.medias().stream().map(UserContent::from).collect(Collectors.toList());
            userContents.add(UserContent.from(chatMessage.text()));
            openAiChatMessage.content = userContents;
        } else {
            openAiChatMessage.content = chatMessage.text();
        }
        openAiChatMessage.toolCallId = chatMessage.id().orElse(null);
        if (CollectionUtils.isNotEmpty(chatMessage.toolCalls())) {
            openAiChatMessage.toolCalls =
                    chatMessage.toolCalls().stream().map(OpenAiToolCall::from).collect(Collectors.toList());
        }
        return openAiChatMessage;
    }

    public Object content() {
        return content;
    }

    public List<OpenAiToolCall> toolCalls() {
        return this.toolCalls;
    }
}
