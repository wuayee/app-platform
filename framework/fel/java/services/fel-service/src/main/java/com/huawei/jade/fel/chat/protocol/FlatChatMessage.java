/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.protocol;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.tool.ToolCall;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 表示聊天消息的传输实现。
 *
 * @author 易文渊
 * @since 2024-04-12
 */
@Data
@NoArgsConstructor
public class FlatChatMessage implements ChatMessage {
    private String id;
    private String type;
    private String text;
    private List<Media> medias;
    private List<ToolCall> toolCalls;

    /**
     * 根据{@link ChatMessage} 构造消息传输对象。
     *
     * @param chatMessage 提供构造参数的 {@link ChatMessage}。
     */
    public FlatChatMessage(ChatMessage chatMessage) {
        Validation.notNull(chatMessage, "The chat message cannot be null.");
        Validation.notNull(chatMessage.type(), "The message type cannot be null.");
        this.id = chatMessage.id().orElse(null);
        this.type = chatMessage.type().name();
        this.text = chatMessage.text();
        this.medias = chatMessage.medias();
        this.toolCalls = chatMessage.toolCalls();
    }

    @Override
    public Optional<String> id() {
        return Optional.ofNullable(this.id);
    }

    @Override
    public MessageType type() {
        Validation.notNull(this.type, "The message type cannot be null.");
        return MessageType.valueOf(StringUtils.toUpperCase(this.type));
    }

    @Override
    public String text() {
        return Optional.ofNullable(this.text).orElse(StringUtils.EMPTY);
    }

    @Override
    public List<Media> medias() {
        return Optional.ofNullable(this.medias).orElseGet(Collections::emptyList);
    }

    @Override
    public List<ToolCall> toolCalls() {
        return Optional.ofNullable(this.toolCalls).orElseGet(Collections::emptyList);
    }
}