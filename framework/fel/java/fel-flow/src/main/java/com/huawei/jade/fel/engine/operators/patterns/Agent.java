/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolProvider;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Agent 基类。
 *
 * @param <I> 表示输入数据的类型。
 * @param <O> 表示处理完成的数据类型。
 * @author 刘信宏
 * @since 2024-04-12
 */
public abstract class Agent<I, O> extends FlowSupportable<I, O> {
    /**
     * 使用 AI 流程提供者初始化 {@link Agent}{@code <}{@link I}{@code , }{@link O}{@code >}。
     *
     * @param flowSupplier 表示AI 流程提供者的 {@link Supplier} {@code <}{@link AiProcessFlow}{@code <}{@link I}{@code ,
     * }{@link O}{@code >}{@code >}。
     * @throws IllegalArgumentException 当 {@code flowSupplier} 为 {@code null} 时。
     */
    public Agent(Supplier<AiProcessFlow<I, O>> flowSupplier) {
        super(flowSupplier);
    }

    /**
     * 执行工具。
     *
     * @param aiMessage 表示大模型响应的 {@link AiMessage}。
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @return 表示工具执行结果的 {@link Prompt}。
     * @throws IllegalArgumentException 当 {@code toolProvider} 或 {@code aiMessage} 为 {@code null} 时。
     */
    protected static Prompt toolCallHandle(ToolProvider toolProvider, AiMessage aiMessage) {
        Validation.notNull(toolProvider, "ToolProvider cannot be null.");
        Validation.notNull(aiMessage, "AiMessage cannot be null.");
        List<FlatChatMessage> collect = Optional.ofNullable(aiMessage.toolCalls())
                .map(m -> m.stream().map(toolProvider::call).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        return ChatMessages.from(collect);
    }

    /**
     * 判断大模型响应中是否存在异步工具。
     *
     * @param aiMessage 表示大模型响应的 {@link AiMessage}。
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @return 如果存在异步工具，则返回 {@code true}，否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code toolProvider} 或 {@code aiMessage} 为 {@code null} 时。
     */
    protected static boolean containAsyncTool(ToolProvider toolProvider, AiMessage aiMessage) {
        Validation.notNull(toolProvider, "ToolProvider cannot be null.");
        Validation.notNull(aiMessage, "AiMessage cannot be null.");
        List<String> toolsName = aiMessage.toolCalls().stream().map(ToolCall::getName).collect(Collectors.toList());
        if (toolsName.isEmpty()) {
            return false;
        }
        return toolProvider.getTool(toolsName).stream().anyMatch(Tool::isAsync);
    }
}
