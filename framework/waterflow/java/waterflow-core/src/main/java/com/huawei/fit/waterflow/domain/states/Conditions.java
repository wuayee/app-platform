/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.states;

import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;

/**
 * 条件节点
 *
 * @param <D> 原始数据类型
 * @param <I> 入参数据类型
 * @param <F> 内部流程类型
 * @since 1.0
 */
public class Conditions<D, I, F extends Flow<D>> extends Activity<D, F> {
    /**
     * node
     */
    protected final State<I, D, I, F> node;

    protected Conditions(State<I, D, I, F> node) {
        super(node.getFlow());
        this.node = node;
    }

    /**
     * 条件节点后面只能跟match：条件分支
     *
     * @param whether 条件判定函数
     * @param processor 该条件分支的处理逻辑
     * @return 得到条件关系，这里不是真实的节点，节点在MatchHappen后面在出来
     */
    public <O> MatchHappen<O, D, I, F> match(Operators.Whether<I> whether,
            Operators.BranchProcessor<O, D, I, F> processor) {
        return new MatchHappen<>(whether, processor, this);
    }

    /**
     * 条件节点后面只能跟match：条件分支
     *
     * @param whether 条件判定函数
     * @param processor 分支命中后的处理逻辑，通常最终是调用to跳转
     * @return 得到条件关系，这里不是真实的节点节点在MatchHappen后面在出来
     */
    public MatchToHappen<D, I, F> matchTo(Operators.Whether<I> whether,
            Operators.BranchToProcessor<D, I, F> processor) {
        return new MatchToHappen<>(whether, processor, this);
    }
}
