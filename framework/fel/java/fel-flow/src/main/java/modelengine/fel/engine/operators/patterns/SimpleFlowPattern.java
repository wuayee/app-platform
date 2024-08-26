/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.engine.operators.patterns;

import modelengine.fel.core.Pattern;
import modelengine.fel.engine.util.AiFlowSession;
import modelengine.fit.waterflow.domain.context.FlowSession;
import modelengine.fit.waterflow.domain.emitters.EmitterListener;
import modelengine.fit.waterflow.domain.stream.operators.Operators;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

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

    /**
     * 使用数据处理器初始化 {@link SimpleFlowPattern}{@code <}{@link I}{@code , }{@link O}{@code >}。
     *
     * @param processor 表示数据处理器的 {@link Operators.Map}{@code <}{@link I}{@code , }{@link O}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public SimpleFlowPattern(Operators.ProcessMap<I, O> processor) {
        this(processor, null);
    }

    public SimpleFlowPattern(Pattern<I, O> pattern) {
        this((data, ctx) -> AiFlowSession.applyPattern(pattern, data, ObjectUtils.cast(ctx)), null);
    }

    private SimpleFlowPattern(Operators.ProcessMap<I, O> processor, EmitterListener<O, FlowSession> handler) {
        this.processor = Validation.notNull(processor, "The processor cannot be null.");
        this.handler = handler;
    }

    @Override
    public O invoke(I data) {
        FlowSession session = AiFlowSession.require();
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
