/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.chat.charactar;

import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.jade.fel.chat.MessageType;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.tool.ToolCall;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示大模型响应的聊天消息实现。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public class AiMessage extends AbstractChatMessage {
    private List<ToolCall> toolCalls = new ArrayList<>();

    /**
     * 通过文本信息来初始化 {@link AiMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    public AiMessage(String text) {
        super(text);
    }

    /**
     * 通过文本信息和工具调用来初始化 {@link AiMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     * @param toolCalls 表示工具调用列表的 {@link List}{@code <}{@link ToolCall}{@code >}。
     */
    public AiMessage(String text, List<ToolCall> toolCalls) {
        super(text);
        this.toolCalls = toolCalls;
    }

    @Override
    public MessageType type() {
        return MessageType.AI;
    }

    @Override
    public List<ToolCall> toolCalls() {
        return this.toolCalls;
    }

    @Override
    public List<Media> medias() {
        return this.contents.medias();
    }

    /**
     * 判断模型响应是否是调用工具。
     *
     * @return 表示是否是调用工具的 {@code boolean}。
     */
    public boolean isToolCall() {
        return CollectionUtils.isNotEmpty(this.toolCalls);
    }

    @Override
    public String toString() {
        return this.isToolCall() ? "ai: " + this.toolCalls.toString() : super.toString();
    }
}
