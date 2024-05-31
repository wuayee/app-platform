/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.flows;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.chat.ChatMessage;
import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.core.memory.Memory;
import com.huawei.jade.fel.engine.activities.FlowCallBack;
import com.huawei.jade.fel.engine.operators.models.ChatChunk;
import com.huawei.jade.fel.engine.operators.models.StreamingConsumer;
import com.huawei.jade.fel.engine.operators.sources.Source;
import com.huawei.jade.fel.engine.util.StateKey;
import com.huawei.jade.fel.tool.ToolContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * AI 数据处理流程对话对象，是启动流程的入口。
 *
 * @param <D> 流程开始节点传入的数据类型。
 * @param <R> 流程结束节点输出的数据类型。
 * @author 刘信宏
 * @since 2024-04-28
 */
public class Conversation<D, R> {
    private final AiProcessFlow<D, R> flow;
    private final FlowSession session;
    private final AtomicReference<ConverseListener<R>> converseListener = new AtomicReference<>(null);
    private FlowCallBack.Builder<R> callBackBuilder;

    /**
     * AI 数据处理流程对话的构造方法。
     *
     * @param flow 表示 AI 流程对象的 {@link AiProcessFlow}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @param session 表示流程会话实例信息的 {@link FlowSession}。
     * @throws IllegalArgumentException 当 {@code flow} 为 {@code null} 时。
     */
    public Conversation(AiProcessFlow<D, R> flow, FlowSession session) {
        this.flow = Validation.notNull(flow, "Flow cannot be null.");
        this.session = (session == null)
                ? this.setConverseListener(new FlowSession())
                : this.setSubConverseListener(session);
        this.callBackBuilder = FlowCallBack.builder();
    }

    /**
     * 往流程注入数据数组，驱动流程流转。
     *
     * @param data 表示待注入数据数组的 {@link D}{@code []}。
     * @return 表示线程同步器的 {@link ConverseLatch}{@code <}{@link R}{@code >}。
     * @throws IllegalStateException 相同对话的上一次 offer 未完成时。
     */
    @SafeVarargs
    public final ConverseLatch<R> offer(D... data) {
        ConverseLatch<R> latch = setListener(this.flow);
        FlowSession newSession = new FlowSession(this.session);
        this.flow.start().offer(data, newSession);
        return latch;
    }

    /**
     * 从一个命名节点批量注入数据，驱动流程流转。
     *
     * @param nodeId 表示节点名称的 {@link String}。
     * @param data 表示数据数组的 {@link List}{@code <}{@code ? extends }{@link Object}{@code >}。
     * @return 表示线程同步器的 {@link ConverseLatch}{@code <}{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code nodeId} 为 {@code null} 、空字符串或只有空白字符的字符串时。
     * @throws IllegalStateException 相同对话的上一次数据注入未完成时。
     */
    public ConverseLatch<R> offer(String nodeId, List<?> data) {
        Validation.notBlank(nodeId, "invalid nodeId.");
        ConverseLatch<R> latch = setListener(this.flow);
        FlowSession newSession = new FlowSession(this.session);
        this.flow.origin().offer(nodeId, data.toArray(new Object[0]), newSession);
        return latch;
    }

    /**
     * 当前会话订阅一个发射源，由发射源驱动会话执行，会话结束后会注销本次订阅。
     * <p>注意发射源的 {@link Source#emit(Object, FlowSession)} 传入的 {@link FlowSession} 的状态数据，会被当前会话的各类
     * {@code bind} 方法覆盖， 如 {@link Conversation#bind(String, Object)}、 {@link Conversation#bind(Memory)} 等。</p>
     *
     * @param source 表示发射源的 {@link Source}{@code <}{@link D}{@code >}。
     * @return 表示线程同步器的 {@link ConverseLatch}{@code <}{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code source} 为 {@code null} 时。
     */
    public ConverseLatch<R> offer(Source<D> source) {
        Validation.notNull(source, "Source can not be null.");

        FlowSession sessionClone = new FlowSession(this.session);
        AiProcessFlow<D, R> processFlow = AiFlows.<D>create().just((data, ctx) -> {
            FlowContext<D> flowContext = ObjectUtils.cast(ctx);
            flowContext.getSession().copySessionState(sessionClone);
        }).delegate(this.flow).close();
        processFlow.offer(source);

        Action finallyCb = this.callBackBuilder.getFinallyCb();
        this.callBackBuilder.doOnFinally(finallyCb.andThen(() -> source.unregister(processFlow)));
        return setListener(processFlow);
    }

    /**
     * 绑定大模型超参数到对话上下文，用于流程后续的大模型节点。
     *
     * @param options 表示大模型超参数的 {@link ChatOptions}。
     * @return 表示绑定了大模型超参数的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code options} 为 {@code null} 时。
     */
    public Conversation<D, R> bind(ChatOptions options) {
        Validation.notNull(options, "Chat options cannot be null.");
        this.session.setInnerState(StateKey.CHAT_OPTIONS, options);
        return this;
    }

    /**
     * 绑定历史记录句柄到对话上下文，只有绑定了历史记录句柄，该对话才会保存历史记录。
     *
     * @param memory 表示历史记录句柄的 {@link Memory}。
     * @return 表示绑定了历史记录句柄的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code memory} 为 {@code null} 时。
     */
    public Conversation<D, R> bind(Memory memory) {
        Validation.notNull(memory, "Memory cannot be null.");
        this.session.setInnerState(StateKey.HISTORY_OBJ, memory);
        return this;
    }

    /**
     * 绑定流式响应信息消费者到对话上下文，用于消费流程流转过程中的流式信息。
     *
     * @param consumer 表示流式响应信息消费者的 {@link StreamingConsumer}{@code <}{@link ChatMessage}{@code ,
     * }{@link ChatChunk}{@code >}。
     * @return 表示绑定了流式响应信息消费者的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code consumer} 为 {@code null} 时。
     */
    public Conversation<D, R> bind(StreamingConsumer<ChatMessage, ChatChunk> consumer) {
        Validation.notNull(consumer, "Streaming consumer cannot be null.");
        this.session.setInnerState(StateKey.STREAMING_CONSUMER, consumer);
        return this;
    }

    /**
     * 绑定自定义参数到对话上下文，后续可以在流程中的如下节点获取：
     * <ul>
     *     <li>{@link com.huawei.jade.fel.engine.activities.AiStart#map(Operators.ProcessMap)}</li>
     *     <li>{@link com.huawei.jade.fel.engine.activities.AiStart#just(Operators.ProcessJust)}</li>
     *     <li>{@link com.huawei.jade.fel.engine.activities.AiStart#process(Operators.Process)}</li>
     *     <li>{@link com.huawei.jade.fel.engine.activities.AiStart#reduce(java.util.function.Supplier,
     *     Operators.ProcessReduce)}</li>
     * </ul>
     *
     * @param key 表示自定义键的 {@link String}。
     * @param value 表示自定义值的 {@link Object}。
     * @return 表示绑定了自定义参数的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     */
    public Conversation<D, R> bind(String key, Object value) {
        if (value != null) {
            this.session.setState(key, value);
        }
        return this;
    }

    /**
     * 绑定自定义参数到对话上下文。用途与 {@link Conversation#bind(String, Object)} 一致。
     *
     * @param ctx 表示自定义键值对的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示绑定了自定义参数的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code ctx} 为 {@code null} 时。
     */
    public Conversation<D, R> bind(Map<String, Object> ctx) {
        Validation.notNull(ctx, "Context map cannot be null.");
        ctx.forEach(this::bind);
        return this;
    }

    /**
     * 绑定自定义工具参数到对话上下文，用于工具调用。
     *
     * @param toolContext 表示自定义工具上下文参数的 {@link ToolContext}。
     * @return 表示绑定了工具上下文参数的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code ctx} 为 {@code null} 时。
     */
    public Conversation<D, R> bind(ToolContext toolContext) {
        Validation.notNull(toolContext, "Tool context cannot be null.");
        this.session.setInnerState(StateKey.TOOL_CONTEXT, toolContext);
        return this;
    }

    /**
     * 设置对话成功回调，本轮对话成功触发该回调。
     *
     * @param processor 表示指定的对话成功时操作的 {@link Consumer}{@code <}{@link R}{@code >}。
     * @return 表示设置完成功回调的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public Conversation<D, R> doOnSuccess(Consumer<R> processor) {
        this.callBackBuilder.doOnSuccess(processor);
        return this;
    }

    /**
     * 设置对话级错误回调，本轮对话发生异常时触发该回调。
     *
     * @param errorHandler 表示指定的对话发生异常时操作的 {@link Consumer}{@code <? extends }{@link Throwable}{@code >}。
     * @return 表示设置完异常回调的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code errorHandler} 为 {@code null} 时。
     */
    public Conversation<D, R> doOnError(Consumer<Throwable> errorHandler) {
        this.callBackBuilder.doOnError(errorHandler);
        return this;
    }

    /**
     * 设置对话结束回调，无论本轮对话成功或失败都会触发该回调。
     *
     * @param finallyAction 表示本轮对话结束时操作的 {@link Action}。
     * @return 表示设置完结束回调的对话对象的 {@link Conversation}{@code <}{@link D}{@code , }{@link R}{@code >}。
     * @throws IllegalArgumentException 当 {@code finallyAction} 为 {@code null} 时。
     */
    public Conversation<D, R> doOnFinally(Action finallyAction) {
        this.callBackBuilder.doOnFinally(finallyAction);
        return this;
    }

    private ConverseLatch<R> setListener(AiProcessFlow<D, R> currFlow) {
        ConverseLatch<R> latch = new ConverseLatch<>();
        Predictable<R> predictable = new Predictable<>(currFlow, this.callBackBuilder.build(), latch);
        ConverseListener<R> listener = this.converseListener.getAndSet(predictable);
        if (listener != null && !listener.isCompleted()) {
            throw new IllegalStateException("conversation is running.");
        }

        AtomicReference<Map<String, AtomicReference<ConverseListener<R>>>> listenerMap =
                this.session.getInnerState(StateKey.CONVERSE_LISTENER);
        listenerMap.get().put(currFlow.getId(), converseListener);

        // 清空临时 builder，用于同一会话的下一次 offer 数据
        this.callBackBuilder = FlowCallBack.builder();
        return latch;
    }

    private FlowSession setSubConverseListener(FlowSession session) {
        FlowSession flowSession = new FlowSession(session);
        if (flowSession.getInnerState(StateKey.CONVERSE_LISTENER) == null) {
            this.setConverseListener(flowSession);
        }
        return flowSession;
    }

    private FlowSession setConverseListener(FlowSession session) {
        session.setInnerState(StateKey.CONVERSE_LISTENER, new AtomicReference<>(new ConcurrentHashMap<>()));
        return session;
    }
}
