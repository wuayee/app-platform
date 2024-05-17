/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import static com.huawei.jade.fel.engine.util.StateKey.HISTORY_INPUT;
import static com.huawei.jade.fel.engine.util.StateKey.STREAMING_CONSUMER;
import static com.huawei.jade.fel.engine.util.StateKey.STREAMING_FLOW_CONTEXT;
import static com.huawei.jade.fel.engine.util.StateKey.STREAMING_PROCESSOR;

import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitter;
import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitterDataBuilder;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.stream.nodes.Retryable;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.content.Media;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.engine.operators.AiRunnableArg;
import com.huawei.jade.fel.tool.ToolCall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 流式模型发射器。
 *
 * @author 刘信宏
 * @since 2024-05-16
 */
public class LlmEmitter<O extends ChatMessage> extends FiniteEmitter<O, ChatChunk> {
    private static final StreamingConsumer<ChatChunk, ChatChunk> EMPTY_CONSUMER = (acc, chunk) -> {};

    private final Memory memory;
    private final StringBuilder text = new StringBuilder();
    private final List<Media> medias = new ArrayList<>();
    private final List<ToolCall> toolCalls = new ArrayList<>();
    private final ChatMessage question;
    private final StreamingConsumer<ChatChunk, ChatChunk> consumer;
    private final Processor<Prompt, ChatChunk> processor;
    private final FlowContext<Prompt> context;

    /**
     * 初始化 {@link LlmEmitter}。
     *
     * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     * @param builder 表示有限流数据构造器的 {@link FiniteEmitterDataBuilder}{@code <}{@link O}{@code ,
     * }{@link ChatChunk}{@code >}。
     * @param arg 表示模型节点运行时参数的 {@link AiRunnableArg}{@code <}{@link Prompt}{@code >}。
     */
    public LlmEmitter(Publisher<O> publisher,
            FiniteEmitterDataBuilder<O, ChatChunk> builder, AiRunnableArg<Prompt> arg) {
        super(arg.getSession(), publisher, builder);
        this.memory = arg.memory();
        this.question = Validation.notNull(arg.getInnerState(HISTORY_INPUT), "Question cant be null.");
        this.consumer = ObjectUtils.nullIf(arg.getInnerState(STREAMING_CONSUMER), EMPTY_CONSUMER);
        this.processor = Validation.notNull(arg.getInnerState(STREAMING_PROCESSOR), "Processor cant be null");
        this.context = Validation.notNull(arg.getInnerState(STREAMING_FLOW_CONTEXT), "FlowContext cant be null");
    }

    @Override
    protected void consumeAction(O source, ChatChunk target) {
        this.text.append(source.text());
        this.medias.addAll(source.medias());
        this.toolCalls.addAll(Optional.ofNullable(source.toolCalls()).orElseGet(Collections::emptyList));
        this.consumer.accept(this.buildAccMessage(), target);
    }

    @Override
    protected void completedAction() {
        ChatChunk acc = this.buildAccMessage();
        if (this.memory != null && !acc.isToolCall()) {
            memory.add(question, acc);
        }
        this.consumer.accept(acc, new ChatChunk(StringUtils.EMPTY).setEnd());
    }

    @Override
    protected void errorAction(Exception cause) {
        Retryable<Prompt> retryable = new Retryable<Prompt>(this.processor.getFlowContextRepo(), processor);
        processor.getErrorHandlers()
                .forEach(error -> error.handle(cause, retryable, Collections.singletonList(context)));
    }

    private ChatChunk buildAccMessage() {
        return new ChatChunk(this.text.toString(), this.medias, this.toolCalls);
    }
}
