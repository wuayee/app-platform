/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.patterns;

import modelengine.fel.engine.operators.models.ChatChunk;
import modelengine.fitframework.inspection.Validation;
import modelengine.fel.chat.ChatMessage;
import modelengine.fel.chat.ChatMessages;
import modelengine.fel.chat.Prompt;
import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fel.tool.ToolProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Agent 基类。
 *
 * @param <I> 表示输入数据的类型。
 * @param <O> 表示处理完成的数据类型。
 * @author 刘信宏
 * @since 2024-04-12
 */
public abstract class AbstractAgent<I, O> extends AbstractFlowPattern<I, O> {
    /**
     * 数据聚合处理器。
     *
     * @param acc 表示聚合数据的 {@link ChatChunk}。
     * @param input 表示单次切片数据的 {@link ChatChunk}。
     * @return 表示聚合数据的 {@link ChatChunk}。
     */
    protected static ChatChunk defaultReduce(ChatChunk acc, ChatChunk input) {
        Validation.notNull(input, "The input data cannot be null.");
        if (acc == null) {
            return new ChatChunk(input.text(), input.toolCalls());
        }
        if (input.isEnd()) {
            return acc;
        }
        acc.merge(input);
        return acc;
    }

    /**
     * 调用工具。
     *
     * @param toolProvider 表示工具提供者的 {@link ToolProvider}。
     * @param message 表示聊天消息的 {@link ChatMessage}。
     * @param toolContext 表示自定义工具上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示工具执行结果的 {@link Prompt}。
     */
    protected static Prompt toolCallHandle(ToolProvider toolProvider, ChatMessage message,
            Map<String, Object> toolContext) {
        List<FlatChatMessage> collect = Optional.ofNullable(message.toolCalls())
                .map(m -> m.stream()
                        .map(toolCall -> toolProvider.call(toolCall, toolContext))
                        .collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
        return ChatMessages.from(collect);
    }
}
