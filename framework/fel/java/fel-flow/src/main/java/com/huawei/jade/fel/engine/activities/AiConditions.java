/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities;

import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.states.Conditions;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.fel.engine.activities.processors.AiBranchProcessor;
import com.huawei.jade.fel.engine.activities.processors.AiBranchToProcessor;
import com.huawei.jade.fel.engine.flows.AiFlow;

/**
 * 条件节点。
 *
 * @param <D> 表示当前节点所属流程的初始数据类型。
 * @param <I> 表示入参数据类型。
 * @param <RF> 表示内部的数据流程类型，是 {@link Flow}{@code <}{@link D}{@code >} 的扩展。
 * @param <F> AI 流程类型，是 {@link AiFlow}{@code <}{@link D}{@code , }{@link RF}{@code >} 的扩展。
 * @author 刘信宏
 * @since 2024-04-28
 */
public class AiConditions<D, I, RF extends Flow<D>, F extends AiFlow<D, RF>> extends AiActivity<D, RF, F> {
    private final Conditions<D, I, RF> conditions;

    /**
     * 初始化 {@link AiConditions}{@code <}{@link D}{@code , }{@link I}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     *
     * @param conditions 表示被装饰的 {@link Conditions}{@code <}{@link D}{@code , }{@link I}{@code ,
     * }{@link RF}{@code >}。
     * @param flow 表示 AI 流程对象的 {@link F}。
     */
    protected AiConditions(Conditions<D, I, RF> conditions, F flow) {
        super(flow);
        this.conditions = Validation.notNull(conditions, "Conditions cannot be null.");
    }

    /**
     * 指定条件和对应的处理器创建条件分支。
     *
     * @param whether 表示匹配条件的 {@link Operators.Whether}{@code <}{@link I}{@code >}。
     * @param processor 表示分支处理器的 {@link AiBranchProcessor}{@code <}{@link O}{@code , }{@link D}{@code ,
     * }{@link I}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @param <O> 表示第一个条件分支指定的返回类型。
     * @return 表示条件分支的 {@link AiMatchHappen}{@code <}{@link O}{@code , }{@link D}{@code ,
     * }{@link I}{@code , }{@link RF}{@code , }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public <O> AiMatchHappen<O, D, I, RF, F> match(Operators.Whether<I> whether,
            AiBranchProcessor<O, D, I, RF, F> processor) {
        Validation.notNull(processor, "Ai branch processor cannot be null.");
        return new AiMatchHappen<>(this.conditions.match(whether,
                node -> processor.process(new AiState<>(node, this.flow())).state), this.flow());
    }

    /**
     * 指定条件和对应的处理器创建条件跳转分支。
     *
     * @param whether 表示匹配条件的 {@link Operators.Whether}{@code <}{@link I}{@code >}。
     * @param processor 表示分支跳转处理器的 {@link AiBranchToProcessor}{@code <}{@link D}{@code , }{@link I}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @return 表示条件跳转分支的 {@link AiMatchToHappen}{@code <}{@link D}{@code , }{@link I}{@code , }{@link RF}{@code ,
     * }{@link F}{@code >}。
     * @throws IllegalArgumentException 当 {@code processor} 为 {@code null} 时。
     */
    public AiMatchToHappen<D, I, RF, F> matchTo(Operators.Whether<I> whether,
            AiBranchToProcessor<D, I, RF, F> processor) {
        Validation.notNull(processor, "Ai branchTo processor cannot be null.");
        return new AiMatchToHappen<>(this.conditions.matchTo(whether,
                node -> processor.process(new AiState<>(node, this.flow()))), this.flow());
    }
}
