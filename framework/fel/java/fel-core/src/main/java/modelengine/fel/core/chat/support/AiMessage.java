/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.chat.support;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fel.core.chat.MessageType;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * 表示大模型响应的 {@link modelengine.fel.core.chat.ChatMessage} 实现。
 *
 * @author 刘信宏
 * @since 2024-4-12
 */
public class AiMessage extends AbstractChatMessage {
    private final List<ToolCall> toolCalls;

    /**
     * 通过文本信息来初始化 {@link AiMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     */
    public AiMessage(String text) {
        this(text, null);
    }

    /**
     * 通过文本信息和工具调用来初始化 {@link AiMessage} 的新实例。
     *
     * @param text 表示文本信息的 {@link String}。
     * @param toolCalls 表示工具调用列表的 {@link List}{@code <}{@link ToolCall}{@code >}。
     */
    public AiMessage(String text, List<ToolCall> toolCalls) {
        super(text);
        this.toolCalls = nullIf(toolCalls, Collections.emptyList());
    }

    @Override
    public MessageType type() {
        return MessageType.AI;
    }

    @Override
    public List<ToolCall> toolCalls() {
        return this.toolCalls;
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
