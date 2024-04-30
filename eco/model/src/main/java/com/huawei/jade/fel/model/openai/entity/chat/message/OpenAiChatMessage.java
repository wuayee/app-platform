/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.chat.message;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.model.openai.entity.chat.message.content.UserContent;
import com.huawei.jade.fel.model.openai.entity.chat.message.tool.OpenAiToolCall;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

/**
 * 此类用于表示 OpenAI 的消息对象。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@AllArgsConstructor
@Builder
@Getter
public class OpenAiChatMessage {
    private final Role role;

    /**
     * 使用 {@link Object} 对类型进行通用表示，有以下两种类型：
     * <b>1. {@link String} 类型，表示单纯文本消息；</b>
     * <b>2. 一个由 {@link UserContent} 组成的列表。</b>
     */
    private final Object content;

    @JsonProperty("tool_call_id")
    private String toolCallId;

    @JsonProperty("tool_calls")
    private List<OpenAiToolCall> toolCalls;

    /**
     * 此构造函数用于接收到模型请求时反序列化响应中的 message 字段。
     *
     * @param role 消息类型。
     * @param content 消息内容。
     * @param toolCalls 模型工具调用结果。
     */
    @ConstructorProperties({"role", "content", "tool_calls"})
    public OpenAiChatMessage(Role role, String content, List<OpenAiToolCall> toolCalls) {
        this.role = role;
        this.content = content;
        this.toolCalls = ObjectUtils.getIfNull(toolCalls, Collections::emptyList);
    }
}
