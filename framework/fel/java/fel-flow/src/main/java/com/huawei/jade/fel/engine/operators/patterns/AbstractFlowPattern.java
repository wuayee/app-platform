/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.AiRunnableArg;

/**
 * 流程委托单元。
 *
 * @author 刘信宏
 * @since 2024-06-04
 */
public abstract class AbstractFlowPattern<I, O> implements AsyncPattern<I, O> {
    private final LazyLoader<AiProcessFlow<I, O>> flowSupplier;

    public AbstractFlowPattern() {
        this.flowSupplier = new LazyLoader<>(this::buildFlow);
    }

    /**
     * 构造处理流程。
     *
     * @return 表示数据处理流程的 {@code <}{@link AiProcessFlow}{@code <}{@link I}{@code , }{@link O}{@code >}。
     */
    protected abstract AiProcessFlow<I, O> buildFlow();

    @Override
    public void register(EmitterListener<O, FlowSession> handler) {
        if (handler != null) {
            this.getFlow().register(handler);
        }
    }

    @Override
    public void emit(O data, FlowSession session) {
        FlowSession flowSession = new FlowSession(session);
        this.getFlow().emit(data, flowSession);
    }

    @Override
    public void offer(I data, FlowSession session) {
        this.getFlow().converse(session).offer(data);
    }

    /**
     * 获取同步委托单元。
     *
     * @return 表示同步委托单元的 {@link SyncPattern}{@code <}{@link I}{@code , }{@link O}{@code >}。
     * @throws IllegalStateException 当流程发生异常时。
     */
    public SyncPattern<I, O> sync() {
        return arg -> {
            AiRunnableArg<I> aiArg = ObjectUtils.cast(arg);
            return this.getFlow().converse(aiArg.getSession()).offer(aiArg.data()).await();
        };
    }

    /**
     * 获取被装饰的流程对象。
     *
     * @return 表示被装饰流程对象的 {@link Flow}{@code <}{@link I}{@code >}。
     */
    public Flow<I> origin() {
        return this.getFlow().origin();
    }

    private AiProcessFlow<I, O> getFlow() {
        return Validation.notNull(this.flowSupplier.get(), "The flow cannot be null.");
    }
}
