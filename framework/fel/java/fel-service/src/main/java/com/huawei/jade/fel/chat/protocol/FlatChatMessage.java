/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.protocol;

import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.tool.ToolCall;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    private MessageType type;
    private String text;
    private List<Media> medias;
    private List<ToolCall> toolCalls;

    /**
     * 根据{@link ChatMessage} 构造消息传输对象。
     *
     * @param chatMessage 提供构造参数的 {@link ChatMessage}。
     */
    public FlatChatMessage(ChatMessage chatMessage) {
        this.id = chatMessage.id().orElse(null);
        this.type = chatMessage.type();
        this.text = chatMessage.text();
        this.medias = chatMessage.medias();
        this.toolCalls = chatMessage.toolCalls();
    }

    @Override
    public Optional<String> id() {
        return Optional.ofNullable(id);
    }

    @Override
    public MessageType type() {
        return type;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public List<Media> medias() {
        return medias;
    }

    @Override
    public List<ToolCall> toolCalls() {
        return new ArrayList<>(toolCalls);
    }
}