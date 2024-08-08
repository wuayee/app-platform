/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.model.openai.entity.chat;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.jade.fel.core.tool.ToolCall;

/**
 * 表示 {@link ToolCall} 的 openai 实现。
 *
 * @author 易文渊
 * @since 2024-08-17
 */
public class OpenAiToolCall implements ToolCall {
    private String id;
    private final String type = "function";
    private FunctionCall function;

    /**
     * 使用 {@link ToolCall} 构造一个新的 {@link OpenAiToolCall}。
     *
     * @param toolCall 表示工具调用的 {@link ToolCall}。
     * @return 表示 openai 工具调用的 {@link OpenAiToolCall} 。
     */
    public static OpenAiToolCall from(ToolCall toolCall) {
        FunctionCall functionCall = new FunctionCall();
        functionCall.name = toolCall.name();
        functionCall.arguments = toolCall.arguments();
        OpenAiToolCall openAiToolCall = new OpenAiToolCall();
        openAiToolCall.id = toolCall.id();
        openAiToolCall.function = functionCall;
        return openAiToolCall;
    }

    @Nonnull
    @Override
    public String id() {
        return this.id;
    }

    @Nonnull
    @Override
    public String name() {
        return this.function.name;
    }

    @Nonnull
    @Override
    public String arguments() {
        return this.function.arguments;
    }

    /**
     * 模型所调用的函数，用于表示 tools_calls 字段中嵌套的 function 字段。
     */
    public static class FunctionCall {
        private String name;
        private String arguments;
    }
}