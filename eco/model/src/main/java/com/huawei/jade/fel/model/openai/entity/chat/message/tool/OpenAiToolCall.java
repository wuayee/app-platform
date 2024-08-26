/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.model.openai.entity.chat.message.tool;

import com.huawei.fitframework.annotation.Property;
import com.huawei.jade.fel.tool.ToolCall;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 模型工具的调用结果，此类用于封装 OpenAI 响应中的 tool_calls 字段。
 *
 * @author 易文渊
 * @author 张庭怿
 * @since 2024-4-30
 */
@Data
public class OpenAiToolCall {
    private String id;

    private String type;

    @JsonProperty("function")
    @Property(name = "function")
    private FunctionCall function;

    /**
     * 构建一个新的 {@link OpenAiToolCall} 。
     *
     * @param id 用于设置 {@link OpenAiToolCall#id} 工具调用的编码。
     * @param name 用于设置 {@link OpenAiToolCall#function#name} 函数名。
     * @param arguments 用于设置 {@link OpenAiToolCall#function#arguments} 函数参数。
     * @return {@link OpenAiToolCall} 。
     */
    public static OpenAiToolCall build(String id, String name, String arguments) {
        FunctionCall functionCall = new FunctionCall();
        functionCall.setName(name);
        functionCall.setArguments(arguments);

        OpenAiToolCall toolCall = new OpenAiToolCall();
        toolCall.setId(id);
        toolCall.setType("function");
        toolCall.setFunction(functionCall);

        return toolCall;
    }

    /**
     * 将 OpenAI 工具调用转化为 FEL 工具调用 {@link ToolCall}。
     *
     * @param openAiToolCall OpenAI 工具调用。
     * @return FEL 工具调用。
     */
    public static ToolCall buildFelToolCall(OpenAiToolCall openAiToolCall) {
        return new ToolCall(openAiToolCall.getId(),
                openAiToolCall.getFunction().getName(),
                openAiToolCall.getFunction().getArguments());
    }

    /**
     * 模型所调用的函数，用于表示 tools_calls 字段中嵌套的 function 字段。
     */
    @Data
    public static class FunctionCall {
        private String name;

        private String arguments;
    }
}
