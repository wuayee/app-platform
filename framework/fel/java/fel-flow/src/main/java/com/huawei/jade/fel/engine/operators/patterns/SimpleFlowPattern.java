/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.getIfNull;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.core.Pattern;
import com.huawei.jade.fel.engine.util.StateKey;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@link FlowPattern} 的简单实现，仅支持注册一个监听器，使用 {@link ThreadPoolExecutor} 创建的线程池执行。
 *
 * @param <I> 表示输入数据类型。
 * @param <O> 表示输出数据类型。
 * @author 刘信宏
 * @since 2024-04-22
 */
public class SimpleFlowPattern<I, O> implements FlowPattern<I, O> {
    private static final Integer MAXIMUM_POOL_SIZE = 50;
    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, MAXIMUM_POOL_SIZE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    private EmitterListener<O, FlowSession> handler;
    private final Operators.ProcessMap<I, O> processor;
    private final Map<String, Object> dynamicArgs;
    private final Pattern<I, O> delegatePattern;

    /**
     * 使用数据处理器初始化 {@link SimpleFlowPattern}{@code <}{@link I}{@code , }{@link O}{@code >}。
     *
     * @param processor 表示数据处理器的 {@link Operators.Map}{@code <}{@link I}{@code , }{@link O}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public SimpleFlowPattern(Operators.ProcessMap<I, O> processor) {
        this(processor, null, null, Collections.emptyMap());
    }

    public SimpleFlowPattern(Pattern<I, O> pattern) {
        this((data, ctx) -> pattern.invoke(data), null, pattern, Collections.emptyMap());
    }

    private SimpleFlowPattern(Operators.ProcessMap<I, O> processor, EmitterListener<O, FlowSession> handler,
            Pattern<I, O> pattern, Map<String, Object> args) {
        this.processor = Validation.notNull(processor, "The processor cannot be null.");
        this.handler = handler;
        this.delegatePattern = pattern;
        this.dynamicArgs = Validation.notNull(args, "The args cannot be null.");
    }

    @Override
    public SimpleFlowPattern<I, O> bind(Map<String, Object> args) {
        Operators.ProcessMap<I, O> newProcessor = this.processor;
        // 避免从 ctx 获取 args，否则相同 session 的多分支并发场景，args 存在相互覆盖的问题。
        if (this.delegatePattern != null) {
            newProcessor = (data, ctx) -> delegatePattern.bind(args).invoke(data);
        }
        return new SimpleFlowPattern<>(newProcessor, this.handler, this.delegatePattern, args);
    }

    @Override
    public O invoke(I data) {
        FlowSession session = getIfNull(cast(this.dynamicArgs.get(StateKey.FLOW_SESSION)), FlowSession::new);
        THREAD_POOL.execute(() -> this.emit(this.processor.process(data, session), session));
        return null;
    }

    @Override
    public void register(EmitterListener<O, FlowSession> handler) {
        if (handler != null) {
            this.handler = handler;
        }
    }

    @Override
    public void emit(O data, FlowSession session) {
        if (this.handler == null) {
            return;
        }
        this.handler.handle(data, session);
    }
}
