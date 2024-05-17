/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities.processors;

import com.huawei.fit.waterflow.domain.flow.Flow;
import com.huawei.jade.fel.engine.activities.AiState;
import com.huawei.jade.fel.engine.flows.AiFlow;

/**
 * 分支处理器，用于在 AI 流程中使用条件分支时，指定对应分支的处理逻辑。
 *
 * @param <O> 表示当前节点的输出数据类型。
 * @param <D> 表示当前节点所属流程的初始数据类型。
 * @param <I> 表示入参数据类型。
 * @param <RF> 表示内部的数据流程类型，是 {@link Flow}{@code <}{@link D}{@code >} 的扩展。
 * @param <F> AI 流程类型，是 {@link AiFlow}{@code <}{@link D}{@code , }{@link RF}{@code >} 的扩展。
 * @author 刘信宏
 * @since 2024-04-28
 */
public interface AiBranchProcessor<O, D, I, RF extends Flow<D>, F extends AiFlow<D, RF>> {
    /**
     * 提供分支的处理逻辑。
     *
     * @param node 表示分支起点的 {@link AiState}{@code <}{@link I}{@code , }{@link D}{@code , }{@link I}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     * @return 表示分支的终点的 {@link AiState}{@code <}{@link O}{@code , }{@link D}{@code , }{@link I}{@code ,
     * }{@link RF}{@code , }{@link F}{@code >}。
     */
    AiState<O, D, ?, RF, F> process(AiState<I, D, I, RF, F> node);
}
