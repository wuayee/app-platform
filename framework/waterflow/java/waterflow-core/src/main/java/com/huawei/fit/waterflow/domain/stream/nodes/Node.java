/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.nodes;

import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.END;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.enums.FlowNodeType;
import com.huawei.fit.waterflow.domain.enums.ParallelMode;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscription;
import com.huawei.fit.waterflow.domain.utils.Identity;

import java.util.List;

/**
 * 中间节点，既是数据发送者，也是数据接受者
 * 由于没有多重继承，node直接继承自To，并生成一个From，引用所有From的实现
 *
 * @param <T>
 * @param <R>
 * @author g00564732
 * @since 1.0
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
    public Node(String streamId, Operators.Map<FlowContext<T>, R> processor, FlowContextRepo repo,
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
    public Node(String streamId, String nodeId, Operators.Map<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
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
    public Node(String streamId, String nodeId, Operators.Produce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * initFrom
     *
     * @param repo contextRepo
     * @param messenger messenger
     * @param locks 流程锁
     * @return From<R>
     */
    protected From<R> initFrom(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        return new From<>(this.getStreamId(), repo, messenger, locks); // node里的from跟随subscriber的streamId
    }

    @Override
    public <O> Processor<O, O> conditions(Operators.Map<R, O> convert, Operators.Whether<R> whether) {
        return this.publisher.conditions(convert, whether);
    }

    @Override
    public <O> Processor<O, O> parallel(ParallelMode mode, Operators.Map<R, O> convert, Operators.Whether<R> whether) {
        return this.publisher.parallel(mode, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> join(Operators.Map<FlowContext<M>, O> processor, Operators.Map<R, M> convert,
            Operators.Whether<R> whether) {
        return this.publisher.join(processor, convert, whether);
    }

    @Override
    public <O> Processor<O, O> just(Operators.Just<FlowContext<O>> processor, Operators.Map<R, O> convert,
            Operators.Whether<R> whether) {
        return this.publisher.just(processor, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> map(Operators.Map<FlowContext<M>, O> processor, Operators.Map<R, M> convert,
            Operators.Whether<R> whether) {
        return this.publisher.map(processor, convert, whether);
    }

    @Override
    public <M, O> Processor<M, O> process(Operators.Process<FlowContext<M>, O> processor, Operators.Map<R, M> convert,
            Operators.Whether<R> whether) {
        return this.publisher.process(processor, convert, whether);
    }

    @Override
    public <O> void subscribe(Subscriber<R, O> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    @Override
    public <M, O> void subscribe(Subscriber<M, O> subscriber, Operators.Map<R, M> convert,
            Operators.Whether<R> whether) {
        this.publisher.subscribe(subscriber, convert, whether);
    }

    @Override
    public <O> void subscribe(String eventId, Subscriber<R, O> subscriber) {
        this.publisher.subscribe(eventId, subscriber);
    }

    @Override
    public <M, O> void subscribe(String eventId, Subscriber<M, O> subscriber, Operators.Map<R, M> convert,
            Operators.Whether<R> whether) {
        this.publisher.subscribe(eventId, subscriber, convert, whether);
    }

    @Override
    public void offer(List<FlowContext<R>> contexts) {
        this.publisher.offer(contexts);
    }

    @Override
    public String offer(R data) {
        return this.publisher.offer(data);
    }

    @Override
    public String offer(R... data) {
        return this.publisher.offer(data);
    }

    @Override
    public String offer(R data, FlowSession session) {
        return this.publisher.offer(data, session);
    }

    @Override
    public String offer(R[] data, FlowSession session) {
        return this.publisher.offer(data, session);
    }

    @Override
    public boolean subscribed() {
        return this.publisher.subscribed();
    }

    @Override
    public List<Subscription<R, ?>> getSubscriptions() {
        return this.publisher.getSubscriptions();
    }

    @Override
    public FlowContextRepo getFlowContextRepo() {
        return this.getRepo();
    }

    /**
     * 开始处理数据
     * 把该publisher里所有的数据都publish到subscription
     *
     * @param batchId 批次id
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
        Operators.Map<FlowContext<R>, R> processor = FlowContext::getData;
        Subscriber<R, R> end = new To<>(this.getStreamId(), null, processor, getRepo(), getMessenger(), getLocks(),
                END);
        this.subscribe(end);
        return end;
    }
}
