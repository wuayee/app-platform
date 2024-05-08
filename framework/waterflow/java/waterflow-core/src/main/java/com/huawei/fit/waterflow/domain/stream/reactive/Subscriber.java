/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.reactive;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.emitters.Emitter;
import com.huawei.fit.waterflow.domain.enums.ProcessType;
import com.huawei.fit.waterflow.domain.stream.nodes.Blocks;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;

import java.util.List;

/**
 * 数据接受者，processor数据在接收者处的处理方式
 *
 * @param <I> 接收的数据类型
 * @param <O> 处理后的数据类型
 * @since 1.0
 */
public interface Subscriber<I, O> extends StreamIdentity, Emitter<O, FlowSession> {
    /**
     * 节点接收请求事件入口
     *
     * @param type 处理类型
     * @param contexts 待处理的上下文
     */
    void accept(ProcessType type, List<FlowContext<I>> contexts);

    /**
     * 设置节点block
     *
     * @param block block
     */
    void block(Blocks.Block<I> block);

    /**
     * 获取节点block
     *
     * @return block
     */
    Blocks.Block<I> block();

    /**
     * 设置节点preFilter
     *
     * @param filter filter
     */
    void preFilter(Operators.Filter<I> filter);

    /**
     * 获取节点preFilter
     *
     * @return preFilter
     */
    Operators.Filter<I> preFilter();

    /**
     * 设置节点postFilter
     *
     * @param filter filter
     */
    void postFilter(Operators.Filter<I> filter);

    /**
     * 获取节点postFilter
     *
     * @return postFilter
     */
    Operators.Filter<I> postFilter();

    /**
     * onSubscribe
     *
     * @param subscription subscription
     */
    void onSubscribe(Subscription<I> subscription);

    /**
     * 节点真正处理context方法onProcess
     *
     * @param contexts contexts
     */
    void onProcess(List<FlowContext<I>> contexts);

    /**
     * onNext
     *
     * @param batchId batchId
     */
    void onNext(String batchId);

    /**
     * afterProcess
     *
     * @param pre pre
     * @param after after
     */
    void afterProcess(List<FlowContext<I>> pre, List<FlowContext<O>> after);

    /**
     * onComplete
     *
     * @param callback callback
     */
    void onComplete(Operators.Just<Callback<FlowContext<O>>> callback);

    /**
     * isAuto
     *
     * @return Boolean
     */
    Boolean isAuto();

    /**
     * nextContexts
     *
     * @param batchId 批次id
     * @return List<FlowContext < O>>
     */
    List<FlowContext<O>> nextContexts(String batchId);

    /**
     * onError
     *
     * @param handler handler
     */
    void onError(Operators.ErrorHandler<I> handler);

    /**
     * onGlobalError
     *
     * @param handler handler
     */
    void onGlobalError(Operators.ErrorHandler handler);

    /**
     * 获取错误处理器列表
     *
     * @return 错误处理器列表
     */
    List<Operators.ErrorHandler> getErrorHandlers();
}
