/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static com.huawei.jade.fel.engine.util.StateKey.HISTORY_INPUT;
import static com.huawei.jade.fel.engine.util.StateKey.STREAMING_CONSUMER;
import static com.huawei.jade.fel.engine.util.StateKey.STREAMING_FLOW_CONTEXT;
import static com.huawei.jade.fel.engine.util.StateKey.STREAMING_PROCESSOR;

import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitter;
import com.huawei.fit.waterflow.bridge.fitflow.FiniteEmitterDataBuilder;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.stream.nodes.Retryable;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fitframework.flowable.Publisher;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.Prompt;
import com.huawei.jade.fel.chat.character.HumanMessage;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;

/**
 * 流式模型发射器。
 *
 * @author 刘信宏
 * @since 2024-05-16
 */
public class LlmEmitter<O extends ChatMessage> extends FiniteEmitter<O, ChatChunk> {
    private static final StreamingConsumer<ChatMessage, ChatChunk> EMPTY_CONSUMER = (acc, chunk) -> {};

    private final ChatChunk chunkAcc = new ChatChunk();
    private final Memory memory;
    private final ChatMessage question;
    private final StreamingConsumer<ChatMessage, ChatChunk> consumer;
    private final Processor<Prompt, ChatChunk> processor;
    private final FlowContext<Prompt> context;

    /**
     * 初始化 {@link LlmEmitter}。
     *
     * @param publisher 表示数据发布者的 {@link Publisher}{@code <}{@link O}{@code >}。
     * @param builder 表示有限流数据构造器的 {@link FiniteEmitterDataBuilder}{@code <}{@link O}{@code ,
     * }{@link ChatChunk}{@code >}。
     * @param prompt 表示模型输入的 {@link Prompt}， 用于获取默认用户问题。
     * @param session 表示流程实例运行标识的 {@link FlowSession}。
     */
    public LlmEmitter(Publisher<O> publisher,
            FiniteEmitterDataBuilder<O, ChatChunk> builder, Prompt prompt, FlowSession session) {
        super(Validation.notNull(session, "The session cannot be null."), publisher, builder);
        this.memory = session.getInnerState(StateKey.HISTORY_OBJ);
        this.question = ObjectUtils.getIfNull(session.getInnerState(HISTORY_INPUT),
                () -> this.getDefaultQuestion(prompt));
        this.consumer = ObjectUtils.nullIf(session.getInnerState(STREAMING_CONSUMER), EMPTY_CONSUMER);
        this.processor = Validation.notNull(
                cast(session.getInnerState(STREAMING_PROCESSOR)), "The processor cannot be null.");
        this.context = Validation.notNull(
                cast(session.getInnerState(STREAMING_FLOW_CONTEXT)), "The flow context cannot be null.");
    }

    @Override
    protected void consumeAction(O source, ChatChunk target) {
        this.chunkAcc.merge(source);
        this.consumer.accept(this.chunkAcc, target);
    }

    @Override
    protected void completedAction() {
        if (this.memory != null && this.chunkAcc.toolCalls().isEmpty()) {
            memory.add(question, this.chunkAcc);
        }
        this.consumer.accept(this.chunkAcc, new ChatChunk().setEnd());
    }

    @Override
    protected void errorAction(Exception cause) {
        Retryable<Prompt> retryable = new Retryable<Prompt>(this.processor.getFlowContextRepo(), processor);
        processor.getErrorHandlers()
                .forEach(error -> error.handle(cause, retryable, Collections.singletonList(context)));
    }

    private ChatMessage getDefaultQuestion(Prompt prompt) {
        int size = prompt.messages().size();
        if (size == 0) {
            return new HumanMessage(StringUtils.EMPTY);
        }
        return prompt.messages().get(size - 1);
    }
}
