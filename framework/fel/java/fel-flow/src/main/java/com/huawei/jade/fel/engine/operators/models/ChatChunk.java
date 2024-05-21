/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitterData;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.content.Content;
import com.huawei.jade.fel.chat.content.Contents;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.chat.content.MediaContent;
import com.huawei.jade.fel.chat.content.TextContent;
import com.huawei.jade.fel.tool.ToolCall;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 大模型流式响应内容片段。
 *
 * @author 刘信宏
 * @since 2024-05-16
 */
public class ChatChunk extends AiMessage implements ChatMessage, FiniteEmitterData {
    private boolean isEnd = false;
    private Throwable throwable = null;

    /**
     * 使用文本初始化 {@link ChatChunk}。
     *
     * @param text 表示文本数据的 {@link String}。
     */
    public ChatChunk(String text) {
        super(text);
    }

    /**
     * 使用文本数据、媒体数据和工具请求初始化 {@link ChatChunk}。
     *
     * @param text 表示字符串数据的 {@link String}。
     * @param medias 表示媒体数据的 {@link List}{@code <}{@link Media}{@code >}。
     * @param toolCalls 表示工具请求的 {@link List}{@code <}{@link ToolCall}{@code >}。
     */
    public ChatChunk(String text, List<Media> medias, List<ToolCall> toolCalls) {
        super(ChatChunk.buildMessageContent(text, medias), toolCalls);
    }

    /**
     * 使用异常句柄初始化 {@link ChatChunk}。
     *
     * @param throwable 表示异常句柄的 {@link Throwable}。
     */
    public ChatChunk(Throwable throwable) {
        super(StringUtils.EMPTY);
        this.throwable = Validation.notNull(throwable, "Throwable cannot be null.");
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

    private static Contents buildMessageContent(String text, List<Media> medias) {
        Validation.notNull(medias, "Medias cannot be null.");
        List<Content> contentList = medias.stream().map(MediaContent::new).collect(Collectors.toList());
        contentList.add(new TextContent(text));
        return Contents.from(contentList);
    }
}
