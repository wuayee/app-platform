/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.streams.nodes;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Processor;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Publisher;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Subscriber;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Subscription;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Identity;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors.FlatMap;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors.Just;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors.Map;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors.Produce;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors.Reduce;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors.Whether;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.To;

import java.util.List;
import java.util.function.Supplier;

/**
 * 中间节点，既是数据发送者，也是数据接受者
 * 由于没有多重继承，node直接继承自To，并生成一个From，引用所有From的实现
 *
 * @param <T>
 * @param <R>
 * @author g00564732
 * @since 2023/08/14
 */
public class Node<T, R> extends To<T, R> implements Processor<T, R>, Identity {
    private final Publisher<R> publisher;

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public Node(String streamId, Map<FlowContext<T>, R> processor, FlowContextRepo repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    public Node(String streamId, FlatMap<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public Node(String streamId, String nodeId, Map<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * m->n处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     */
    public Node(String streamId, Produce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * m->n处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public Node(String streamId, String nodeId, Produce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     */
    public Node(String streamId, Reduce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public Node(String streamId, String nodeId, Reduce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param publisherSupplier 由子类提供构建publisher的方法
     * @param locks 流程锁
     */
    protected Node(String streamId, Map<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, Supplier<Publisher<R>> publisherSupplier) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = publisherSupplier.get();
    }

    /**
     * initFrom
     *
     * @param repo contextRepo
     * @param messenger messenger
     * @param locks 流程锁
     * @return From<R>
     */
    private From<R> initFrom(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        From<R> from = new From<>(this.getStreamId(), repo, messenger, locks); // node里的from跟随subscriber的streamId
        from.setId(this.getId());
        return from;
    }

    @Override
    public <O> Processor<O, O> conditions(Just<FlowContext<O>> processor, Map<R, O> convert, Whether<R> whether) {
        return this.publisher.conditions(processor, convert, whether);
    }

    @Override
    public <O> Processor<O, O> parallel(Just<FlowContext<O>> processor, ParallelMode mode, Map<R, O> convert,
            Whether<R> whether) {
        return this.publisher.parallel(processor, mode, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> join(Reduce<FlowContext<M>, O> processor, Map<R, M> convert, Whether<R> whether) {
        return this.publisher.join(processor, convert, whether);
    }

    @Override
    public <O> Processor<O, O> just(Just<FlowContext<O>> processor, Map<R, O> convert, Whether<R> whether) {
        return this.publisher.just(processor, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> map(Map<FlowContext<M>, O> processor, Map<R, M> convert, Whether<R> whether) {
        return this.publisher.map(processor, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> flatMap(FlatMap<FlowContext<M>, O> processor, Map<R, M> convert, Whether<R> whether) {
        return this.publisher.flatMap(processor, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> reduce(Reduce<FlowContext<M>, O> processor, Map<R, M> convert, Whether<R> whether) {
        return this.publisher.reduce(processor, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> produce(Produce<FlowContext<M>, O> processor, Map<R, M> convert, Whether<R> whether) {
        return this.publisher.produce(processor, convert, whether);
    }

    @Override
    public <O> void subscribe(Subscriber<R, O> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    @Override
    public <M, O> void subscribe(Subscriber<M, O> subscriber, Map<R, M> convert, Whether<R> whether) {
        this.publisher.subscribe(subscriber, convert, whether);
    }

    @Override
    public <O> void subscribe(String eventId, Subscriber<R, O> subscriber) {
        this.publisher.subscribe(eventId, subscriber);
    }

    @Override
    public <M, O> void subscribe(String eventId, Subscriber<M, O> subscriber, Map<R, M> convert, Whether<R> whether) {
        this.publisher.subscribe(eventId, subscriber, convert, whether);
    }

    @Override
    public void offer(List<FlowContext<R>> contexts) {
        this.publisher.offer(contexts);
    }

    @Override
    public FlowOfferId offer(R data) {
        return this.publisher.offer(data);
    }

    @Override
    public FlowOfferId offer(R... data) {
        return this.publisher.offer(data);
    }

    @Override
    public FlowOfferId offer(R data, FlowTrans trans) {
        return this.publisher.offer(data, trans);
    }

    @Override
    public FlowOfferId offer(R[] data, FlowTrans trans) {
        return this.publisher.offer(data, trans);
    }

    @Override
    public boolean subscribed() {
        return this.publisher.subscribed();
    }

    @Override
    public List<Subscription<R, ?>> getSubscriptions() {
        return this.publisher.getSubscriptions();
    }

    /**
     * 开始处理数据
     * 把该publisher里所有的数据都publish到subscription
     *
     * @param batchId batchId
     */
    @Override
    public void onNext(String batchId) {
        this.publisher.offer(this.nextContexts(batchId));
    }

    /**
     * 关闭到最终subscriber
     *
     * @return subscriber
     */
    @Override
    public Subscriber<R, R> close() {
        Subscriber<R, R> end = new To<>(this.getStreamId(), this.getId(), (Map<FlowContext<R>, R>) i -> i.getData(),
                getRepo(), getMessenger(), getLocks(), FlowNodeType.END);
        this.subscribe(end);
        return end;
    }
}
