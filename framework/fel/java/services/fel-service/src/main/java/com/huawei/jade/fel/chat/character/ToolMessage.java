/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.character;

import com.huawei.jade.fel.chat.MessageType;

import java.util.Optional;

/**
 * 表示工具消息的实现。
 *
 * @author 易文渊
 * @since 2024-4-3
 */
public class ToolMessage extends AbstractChatMessage {
    private final String id;

    /**
     * 通过请求id和工具响应来初始化 {@link ToolMessage} 的新实例。
     *
     * @param id 表示请求编码的 {@link String}。
     * @param text 表示工具响应的 {@link String}。
     */
    public ToolMessage(String id, String text) {
        super(text);
        this.id = id;
    }

    @Override
    public Optional<String> id() {
        return Optional.ofNullable(this.id);
    }

    @Override
    public MessageType type() {
        return MessageType.TOOL;
    }

    @Override
    public String toString() {
        return "Tool " + this.id + ": " + this.text();
    }
}