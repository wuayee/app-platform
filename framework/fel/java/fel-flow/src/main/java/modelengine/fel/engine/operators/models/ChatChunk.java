/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.models;

import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitterData;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.MessageType;
import modelengine.fel.tool.ToolCall;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 大模型流式响应内容片段。
 *
 * @author 刘信宏
 * @since 2024-05-16
 */
@NoArgsConstructor
public class ChatChunk implements ChatMessage, FiniteEmitterData {
    private boolean isEnd = false;
    private Throwable throwable = null;
    private final StringBuilder text = new StringBuilder();
    private final List<ToolCall> toolCalls = new ArrayList<>();

    /**
     * 使用文本数据、媒体数据和工具请求初始化 {@link ChatChunk}。
     *
     * @param text 表示字符串数据的 {@link String}。
     * @param toolCalls 表示工具请求的 {@link List}{@code <}{@link ToolCall}{@code >}。
     */
    public ChatChunk(String text, List<ToolCall> toolCalls) {
        this.merge(text, toolCalls);
    }

    /**
     * 使用异常句柄初始化 {@link ChatChunk}。
     *
     * @param throwable 表示异常句柄的 {@link Throwable}。
     */
    public ChatChunk(Throwable throwable) {
        this.throwable = Validation.notNull(throwable, "Throwable cannot be null.");
    }

    /**
     * 合并文本数据、媒体数据和工具请求 。
     *
     * @param text 表示字符串数据的 {@link String}。
     * @param toolCalls 表示工具请求的 {@link List}{@code <}{@link ToolCall}{@code >}。
     */
    public void merge(String text, List<ToolCall> toolCalls) {
        this.text.append(ObjectUtils.nullIf(text, StringUtils.EMPTY));
        this.toolCalls.addAll(ObjectUtils.getIfNull(toolCalls, Collections::emptyList));
    }

    /**
     * 聚合流式响应内容片段。
     *
     * @param message 表示大模型流式响应内容片段的 {@link ChatMessage}。
     */
    public void merge(ChatMessage message) {
        Validation.notNull(message, "Chat message can not be null.");
        this.merge(message.text(), message.toolCalls());
    }

    /**
     * 设置流式片段结束标记。
     *
     * @return 表示大模型流式响应内容片段的 {@link ChatChunk}。
     */
    public ChatChunk setEnd() {
        this.isEnd = true;
        return this;
    }

    @Override
    public boolean isEnd() {
        return this.isEnd;
    }

    @Override
    public boolean isError() {
        return this.throwable != null;
    }

    @Override
    public String getErrorMessage() {
        return Optional.ofNullable(this.throwable).map(Throwable::getLocalizedMessage).orElse(StringUtils.EMPTY);
    }

    @Override
    public MessageType type() {
        return MessageType.AI;
    }

    @Override
    public String text() {
        return this.text.toString();
    }

    @Override
    public List<ToolCall> toolCalls() {
        return this.toolCalls;
    }

    @Override
    public String toString() {
        String textVal = this.toolCalls.isEmpty() ? this.text() : this.toolCalls.toString();
        return this.type().getRole() + ": " + textVal;
    }
}
