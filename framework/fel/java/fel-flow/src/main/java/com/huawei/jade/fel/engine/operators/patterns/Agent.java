/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.chat.ChatMessages;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.AiMessage;
import com.huawei.jade.fel.chat.protocol.FlatChatMessage;
import com.huawei.jade.fel.engine.activities.processors.AiToolProcessMap;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolCall;
import com.huawei.jade.fel.tool.ToolContext;
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
     * 获取工具执行器。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param agentMsgKey 表示 Agent 响应的所在键的 {@link String}。
     * @return 表示工具执行器 {@link AiToolProcessMap}{@code <}{@link AiMessage}{@code , }{@link AiMessage}{@code >}。
     * @throws IllegalArgumentException
     * <ln>
     *     <li>当 {@code toolProvider} 为 {@code null} 时。</li>
     *     <li>当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。</li>
     * </ln>
     */
    protected static AiToolProcessMap<AiMessage, AiMessage> getToolProcessMap(ToolProvider toolProvider,
            String agentMsgKey) {
        Validation.notNull(toolProvider, "ToolProvider cannot be null.");
        Validation.notBlank(agentMsgKey, "Agent message key cannot be blank.");
        return (input, context, toolContext) -> {
            ChatMessages lastRequest = context.getState(agentMsgKey);
            lastRequest.add(input);
            lastRequest.addAll(Agent.toolCallHandle(toolProvider, input, toolContext).messages());
            return input;
        };
    }

    /**
     * 将 Agent 的 {@link Prompt} 输入信息保存在自定义上下文。
     *
     * @param agentMsgKey 表示 Agent 响应的所在自定义键的 {@link String}。
     * @return 表示数据处理器的 {@link Operators.ProcessJust}{@code <}{@link Prompt}{@code >}。
     * @throws IllegalArgumentException 当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    protected static Operators.ProcessJust<Prompt> putAgentMsg(String agentMsgKey) {
        Validation.notBlank(agentMsgKey, "Agent message key cannot be blank.");
        return (input, context) -> context.setState(agentMsgKey, ChatMessages.from(input.messages()));
    }

    /**
     * 自定义上下文获取 Agent 的 {@link Prompt} 信息。
     *
     * @param agentMsgKey 表示 Agent 响应的所在自定义键的 {@link String}。
     * @return 表示数据处理器的 {@link Operators.ProcessMap}{@code <}{@link AiMessage}{@code , }{@link Prompt}{@code >}。
     * @throws IllegalArgumentException 当 {@code agentMsgKey} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     */
    protected static Operators.ProcessMap<AiMessage, Prompt> getAgentMsg(String agentMsgKey) {
        Validation.notBlank(agentMsgKey, "Agent message key cannot be blank.");
        return (message, ctx) -> ctx.getState(agentMsgKey);
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
        List<String> toolsName = Optional.ofNullable(aiMessage.toolCalls())
                .map(m -> m.stream().map(ToolCall::getName).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
        if (toolsName.isEmpty()) {
            return false;
        }
        return toolProvider.getTool(toolsName).stream().anyMatch(Tool::isAsync);
    }

    /**
     * 判断 Agent 响应是否完成。
     *
     * @param aiMessage 表示大模型响应的 {@link AiMessage}。
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @return 如果完成，则返回 {@code true}，否则，返回 {@code false}。
     * @throws IllegalArgumentException 当 {@code toolProvider} 或 {@code aiMessage} 为 {@code null} 时。
     */
    protected static boolean isFinish(ToolProvider toolProvider, AiMessage aiMessage) {
        Validation.notNull(toolProvider, "ToolProvider cannot be null.");
        Validation.notNull(aiMessage, "AiMessage cannot be null.");
        return !aiMessage.isToolCall() || Agent.containAsyncTool(toolProvider, aiMessage);
    }

    private static Prompt toolCallHandle(ToolProvider toolProvider, AiMessage aiMessage, ToolContext toolContext) {
        List<FlatChatMessage> collect = Optional.ofNullable(aiMessage.toolCalls())
                .map(m -> m.stream()
                        .map(toolCall -> toolProvider.call(toolCall, toolContext))
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
        return ChatMessages.from(collect);
    }
}
