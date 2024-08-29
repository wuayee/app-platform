/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.entity.chat.message;

import modelengine.fel.model.openai.entity.chat.message.content.UserContent;
import modelengine.fel.model.openai.entity.chat.message.tool.OpenAiToolCall;
import modelengine.fel.tool.ToolCall;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.serialization.annotation.SerializeStrategy;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 此类用于表示 OpenAI 的消息对象。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@AllArgsConstructor
@Builder
@NoArgsConstructor
@SerializeStrategy(include = SerializeStrategy.Include.NON_NULL)
public class OpenAiChatMessage {
    private String role;

    /**
     * 使用 {@link Object} 对类型进行通用表示，有以下两种类型：
     * <b>1. {@link String} 类型，表示单纯文本消息；</b>
     * <b>2. 一个由 {@link UserContent} 组成的列表。</b>
     */
    @Getter
    private Object content;

    @Property(name = "tool_call_id")
    @JsonProperty("tool_call_id")
    @Getter
    private String toolCallId;

    @Property(name = "tool_calls")
    @JsonProperty("tool_calls")
    @Getter
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
        this.role = role.getValue();
        this.content = content;
        this.toolCalls = ObjectUtils.getIfNull(toolCalls, Collections::emptyList);
    }

    /**
     * 获取角色。
     *
     * @return 表示角色的 {@link Role}。
     */
    public Role getRole() {
        return Role.valueOf(StringUtils.toUpperCase(this.role));
    }

    /**
     * 获取消息内容，使用 {@link Object} 对类型进行通用表示，有以下两种类型：
     * <ol>
     *     <li>{@link String} 类型，表示单纯文本消息；</li>
     *     <li>由 {@link UserContent} 组成的列表。</li>
     * </ol>
     *
     * @return 表示消息内容的 {@link Object}。
     */
    public Object content() {
        return this.content;
    }

    /**
     * 获取消息的工具调用。
     *
     * @return 表示工具调用的 {@link List}{@code <}{@link ToolCall}{@code >}。
     */
    public List<ToolCall> toolCalls() {
        return Optional.ofNullable(this.toolCalls)
                .map(t -> t.stream().map(OpenAiToolCall::buildFelToolCall).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }
}
