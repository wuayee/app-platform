/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.patterns;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.engine.flows.AiProcessFlow;

import java.util.function.Supplier;

/**
 * 异步委托单元的 AI 流程实现。
 *
 * @param <I> 表示输入数据的类型。
 * @param <O> 表示流程处理完成的数据类型。
 * @author 刘信宏
 * @since 2024-04-22
 */
public class FlowSupportable<I, O> extends AbstractFlowPattern<I, O> {
    private final Supplier<AiProcessFlow<I, O>> flowSupplier;

    /**
     * 通过 AI 流程提供者初始化 {@link FlowSupportable}{@code <}{@link I}{@code , }{@link O}{@code >}。
     *
     * @param flowSupplier 表示AI 流程提供者的 {@link Supplier}{@code <}{@link AiProcessFlow}{@code <}{@link I}{@code ,
     * }{@link O}{@code >}{@code >}。
     * @throws IllegalArgumentException 当 {@code flowSupplier} 为 {@code null} 时。
     */
    public FlowSupportable(Supplier<AiProcessFlow<I, O>> flowSupplier) {
        this.flowSupplier = Validation.notNull(flowSupplier, "Flow supplier cannot be null.");
    }

    @Override
    protected AiProcessFlow<I, O> buildFlow() {
        return this.flowSupplier.get();
    }
}
