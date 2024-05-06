/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import static com.huawei.jade.fel.engine.util.SessionUtils.copyFlowSession;

import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;
import com.huawei.jade.fel.engine.operators.AiRunnableArg;

import java.util.function.Supplier;

/**
 * 异步委托单元的 AI 流程实现。
 *
 * @param <I> 表示输入数据的类型。
 * @param <O> 表示流程处理完成的数据类型。
 * @author 刘信宏
 * @since 2024-04-22
 */
public class FlowSupportable<I, O> implements AsyncPattern<I, O> {
    private final AiProcessFlow<I, O> flow;

    /**
     * 通过 AI 流程提供者初始化 {@link FlowSupportable}{@code <}{@link I}{@code , }{@link O}{@code >}。
     *
     * @param flowSupplier 表示AI 流程提供者的 {@link Supplier}{@code <}{@link AiProcessFlow}{@code <}{@link I}{@code ,
     * }{@link O}{@code >}{@code >}。
     * @throws IllegalArgumentException 当 {@code flowSupplier} 为 {@code null} 时。
     */
    public FlowSupportable(Supplier<AiProcessFlow<I, O>> flowSupplier) {
        this.flow = Validation.notNull(flowSupplier.get(), "Flow cannot be null.");
    }

    @Override
    public void register(EmitterListener<O, FlowSession> handler) {
        if (handler != null) {
            this.flow.register(handler);
        }
    }

    @Override
    public void emit(O data, FlowSession session) {
        FlowSession flowSession = copyFlowSession(session);
        this.flow.emit(data, flowSession);
    }

    @Override
    public void offer(I data, FlowSession session) {
        this.flow.converse(session).offer(data);
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
            try {
                return this.flow.converse(aiArg.getSession()).offer(aiArg.data()).await();
            } catch (InterruptedException exception) {
                throw new IllegalStateException(exception);
            }
        };
    }
}
