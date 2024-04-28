/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.reactive;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.emitters.EmitterListener;
import com.huawei.fit.waterflow.domain.enums.ParallelMode;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;

import java.util.List;

/**
 * 数据发布者
 * 发布数据的时候同时确定接收者的数据处理方式
 * map: 1:1数据处理方式
 * reduce: n:1数据处理方式
 * produce: m:n数据处理方式
 *
 * @param <I> 要发布的数据类型
 * @since 1.0
 */
public interface Publisher<I> extends StreamIdentity, EmitterListener<I, FlowSession> {
    /**
     * 处理数据，即向后续节点offer
     *
     * @param data 待offer的数据
     */
    default void handle(I data) {
        this.offer(data);
    }

    @Override
    default void handle(I data, FlowSession flowSession) {
        this.offer(data, new FlowSession(flowSession));
    }

    /**
     * conditions
     *
     * @param convert 数据转换器
     * @param whether 判定条件
     * @param <O> 转换的目标类型
     * @return Processor<O, O>
     */
    <O> Processor<O, O> conditions(Operators.Map<I, O> convert, Operators.Whether<I> whether);

    /**
     * parallel
     *
     * @param mode mode
     * @param convert convert
     * @param whether whether
     * @return Processor<O, O>
     */
    <O> Processor<O, O> parallel(ParallelMode mode, Operators.Map<I, O> convert, Operators.Whether<I> whether);

    /**
     * join
     *
     * @param processor processor
     * @param convert convert
     * @param whether whether
     * @return Processor<M, O>
     */
    <M, O> Processor<M, O> join(Operators.Map<FlowContext<M>, O> processor, Operators.Map<I, M> convert,
            Operators.Whether<I> whether);

    /**
     * just
     *
     * @param processor processor
     * @param convert convert
     * @param whether whether
     * @return Processor<O, O>
     */
    <O> Processor<O, O> just(Operators.Just<FlowContext<O>> processor, Operators.Map<I, O> convert,
            Operators.Whether<I> whether);

    /**
     * map
     *
     * @param processor processor
     * @param convert convert
     * @param whether whether
     * @return Processor<M, O>
     */
    <M, O> Processor<M, O> map(Operators.Map<FlowContext<M>, O> processor, Operators.Map<I, M> convert,
            Operators.Whether<I> whether);

    /**
     * process处理，并往下发射新的数据，支持操作 session KV状态数据
     *
     * @param processor 携带数据、KV下文和发射器的处理器
     * @param convert convert
     * @param whether whether
     * @return Processor<M, O>
     */
    <M, O> Processor<M, O> process(Operators.Process<FlowContext<M>, O> processor, Operators.Map<I, M> convert,
            Operators.Whether<I> whether);

    /**
     * subscribe
     *
     * @param subscriber subscriber
     */
    <O> void subscribe(Subscriber<I, O> subscriber);

    /**
     * subscribe
     *
     * @param subscriber subscriber
     * @param convert convert
     * @param whether whether
     */
    <M, O> void subscribe(Subscriber<M, O> subscriber, Operators.Map<I, M> convert, Operators.Whether<I> whether);

    /**
     * subscribe
     *
     * @param eventId eventId
     * @param subscriber subscriber
     */
    <O> void subscribe(String eventId, Subscriber<I, O> subscriber);

    /**
     * subscribe
     *
     * @param eventId eventId
     * @param subscriber subscriber
     * @param convert convert
     * @param whether whether
     */
    <M, O> void subscribe(String eventId, Subscriber<M, O> subscriber, Operators.Map<I, M> convert,
            Operators.Whether<I> whether);

    /**
     * offer
     *
     * @param contexts contexts
     */
    void offer(List<FlowContext<I>> contexts);

    /**
     * offer
     *
     * @param data data
     * @return String
     */
    String offer(I data);

    /**
     * 在指定的session中offer一个数据
     *
     * @param data 待offer的数据
     * @param session 指定的session
     * @return 本次offer产生的traceId
     */
    String offer(I data, FlowSession session);

    /**
     * 在指定的session中offer一组数据
     *
     * @param data 待offer的数据
     * @param session 指定的session
     * @return 本次offer产生的traceId
     */
    String offer(I[] data, FlowSession session);

    /**
     * offer
     *
     * @param data data
     * @return String
     */
    String offer(I... data);

    /**
     * subscribed
     *
     * @return boolean
     */
    boolean subscribed();

    /**
     * getSubscriptions
     *
     * @return List<Subscription < I, ?>>
     */
    List<Subscription<I, ?>> getSubscriptions();

    /**
     * 获取context repo
     *
     * @return repo
     */
    FlowContextRepo getFlowContextRepo();
}
