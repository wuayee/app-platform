/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.nodes;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_ENGINE_INVALID_MANUAL_TASK;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowTrace;
import com.huawei.fit.waterflow.domain.context.FlowSession;
import com.huawei.fit.waterflow.domain.context.WindowToken;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.contextdata.GlobalFileData;
import com.huawei.fit.waterflow.domain.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.domain.enums.FlowTraceStatus;
import com.huawei.fit.waterflow.domain.enums.ParallelMode;
import com.huawei.fit.waterflow.domain.utils.IdGenerator;
import com.huawei.fit.waterflow.domain.stream.reactive.When;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscription;
import com.huawei.fit.waterflow.domain.utils.UUIDUtil;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 数据发送者
 * offer到数据后就通过when将数据推给接受者
 * streamId第一次由from自动生成
 * streamId后续恢复执行时由用户传入
 *
 * @author g00564732
 * @since 2023/08/14
 */
@SuppressWarnings( {"unchecked", "rawtypes"})
public class From<I> extends IdGenerator implements Publisher<I> {
    /**
     * contextRepo
     */
    protected final FlowContextRepo repo;

    /**
     * messenger
     */
    protected final FlowContextMessenger messenger;

    /**
     * locks
     */
    protected final FlowLocks locks;

    private final List<Subscription<I, ?>> whens = new ArrayList<>();

    private final String streamId;

    public From(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this(null, repo, messenger, locks);
        // 单纯的from的id就是stream id，因为单纯的from是数据的起点，其他都是subscriber
    }

    public From(String streamId, FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this.streamId = streamId != null && !streamId.trim().equals("") ? streamId : this.id;
        this.repo = repo;
        this.messenger = messenger;
        this.locks = locks;
    }

    /**
     * From
     *
     * @param streamId streamId
     * @param nodeId nodeId
     * @param repo contextRepo
     * @param messenger messenger
     * @param locks 流程锁
     */
    public From(String streamId, String nodeId, FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this(streamId, repo, messenger, locks);
        this.id = nodeId;
    }

    /**
     * 数据发送给条件processor
     *
     * @param convert convert
     * @param whether whether
     * @return 新的条件processor
     */
    @Override
    public <O> Processor<O, O> conditions(Operators.Map<I, O> convert, Operators.Whether<I> whether) {
        ConditionsNode<O> node = new ConditionsNode<>(this.getStreamId(), repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    /**
     * 数据发送给平行（广播式）processor
     *
     * @param mode either还是all
     * @param convert convert
     * @param whether whether
     * @return 新的条件processor
     */
    @Override
    public <O> Processor<O, O> parallel(ParallelMode mode, Operators.Map<I, O> convert, Operators.Whether<I> whether) {
        ParallelNode<O> node = new ParallelNode<>(this.getStreamId(), mode,repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    /**
     * 数据发送给join processor
     * 这里的限制不够，要在上层做更多的限制，只有parallel后面的节点才可以join
     *
     * @param processor map处理器
     * @param convert convert
     * @param whether whether
     * @return 新的join processor
     */
    @Override
    public <M, O> Processor<M, O> join(
            Operators.Map<FlowContext<M>, O> processor, Operators.Map<I, M> convert, Operators.Whether<I> whether) {
        JoinNode<M, O> node = new JoinNode<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, i -> true);
        return node;
    }

    /**
     * 数据发送给 just processor，数据只处理，不转换
     *
     * @param processor just处理器
     * @param convert convert
     * @param whether whether
     * @return 新的join processor
     */
    @Override
    public <O> Processor<O, O> just(Operators.Just<FlowContext<O>> processor, Operators.Map<I, O> convert, Operators.Whether<I> whether) {
        // just的实现就是一个返回自己的map
        Node<O, O> node = new Node<>(this.getStreamId(), (Operators.Map<FlowContext<O>, O>) i -> {
            processor.process(i);
            return i.getData();
        }, repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    /**
     * 接收者是1:1数据处理：进1条A数据，返回一条B数据
     *
     * @param processor 处理函数
     * @param convert 处理前when的转换函数
     * @param whether whether
     * @return 返回一个node，这个node是接收者，同时可以继续发送数据
     */
    @Override
    public <M, O> Processor<M, O> map(
            Operators.Map<FlowContext<M>, O> processor, Operators.Map<I, M> convert, Operators.Whether<I> whether) {
        Node<M, O> node = new Node<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    @Override
    public <M, O> Processor<M, O> process(Operators.Process<FlowContext<M>, O> processor,
                                          Operators.Map<I, M> convert, Operators.Whether<I> whether) {
        AtomicReference<Node<M, O>> processRef = new AtomicReference<>();
        Operators.Map<FlowContext<M>, O> wrapper = input -> {
            processor.process(input, input, data -> processRef.get().offer(data, input.getSession()));
            return null;
        };
        Node<M, O> node = new Node<>(this.getStreamId(), wrapper, repo, messenger, locks);
        processRef.set(node);
        this.subscribe(node, convert, whether);
        return node;
    }



    public String offer(I data, FlowSession session){
        I[] array = (I[]) new Object[1];
        array[0] = data;
        return this.offer(array,session);
    }

    public String offer(I[] data, FlowSession trans){
        FlowTrace trace = new FlowTrace();
        Set<String> traceId = new HashSet<>();
        traceId.add(trace.getId());
        List<FlowContext<I>> contexts = Arrays.stream(data)
                .map(d -> new FlowContext<>(this.getStreamId(), this.getId(), d, traceId, this.getId()).inFlowTrans(
                        trans))
                .collect(Collectors.toList());
        WindowToken<I> windowToken = new WindowToken<>(inputs -> inputs.size()==contexts.size());
        contexts.forEach(context->{
            context.setWindowToken(windowToken);
            windowToken.addOrigin(context.getData());
            windowToken.addToDo(context.getData());
        });
        this.offer(startNodeMarkAsHandled(contexts, trace));
        return trace.getId();
    }
    /**
     * publish单条数据
     * 外部使用：controller中的start flow有使用，且返回给了前端
     * 内部使用：全部是单元测试在使用
     *
     * @param data 单条数据
     * @return 流程事务ID
     */
    @Override
    public String offer(I data) {
        return this.offer(data,new FlowSession());
    }

    /**
     * publish 多条数据
     * 外部使用：暂无
     * 内部使用：全部是单元测试在使用
     *
     * @param data 多条数据
     * @return 流程事务ID
     */
    @Override
    public String offer(I... data) {
        return this.offer(data,new FlowSession());
    }

    /**
     * publish stream context，通常这些数据来源于上一个publisher
     * 这个数据可以来源于数据最开始，也可以是接受者处理完的数据
     *
     * @param contexts 数据上下文，里面有要处理的数据，还有其他流处理状态信息
     */
    @Override
    public void offer(List<FlowContext<I>> contexts) {
        if (CollectionUtils.isEmpty(contexts)) {
            return;
        }
        // 每一次offer新数据,atom是不一样的，但是context的streamId是一样的,flowTransId标记是否属于同一次流程运行实例
        if (contexts.stream().map(c -> c.getSession().getId()).distinct().count() != 1) {
            return;
        }

        FlowContext<I> context = contexts.get(0); // 用第一个元素做同源判断
        List<Subscription<I, ?>> qualifiedWhens = this.getSubscriptions();

        // 处理从A流程跳出到B流程,再从B流程调回A流程的指定节点并从该指定节点继续执行A的后续流程
        List<Subscription<I, ?>> sourceWhens = this.getSubscriptions()
            .stream()
            .filter(w -> !context.getStreamId().equals(this.getStreamId()) && w.getTo()
                .getStreamId()
                .equals(context.getStreamId()))
            .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(sourceWhens)) {
            qualifiedWhens = sourceWhens;
        }

        // qualifiedWhens表示的与from节点连接的所有事件，条件节点符合条件的事件在这里筛选，在事件上处理需要下发的context
        qualifiedWhens.forEach(
            w -> w.cache(contexts.stream().filter(c -> w.getWhether().is(c.getData())).collect(Collectors.toList())));
    }

    /**
     * 是否有publisher目标
     * 用于stream闭环时将没有subscribed的publisher关闭到close subscriber
     *
     * @return 是否有
     */
    @Override
    public boolean subscribed() {
        return this.getSubscriptions().size() > 0;
    }

    @Override
    public List<Subscription<I, ?>> getSubscriptions() {
        return new ArrayList<>(this.whens);
    }

    @Override
    public FlowContextRepo getFlowContextRepo() {
        return this.repo;
    }

    /**
     * publish到某subscriber
     * just,map,reduce,produce可以生成subscriber，这个是直接指定subscriber
     *
     * @param subscriber 订阅者
     */
    @Override
    public <O> void subscribe(Subscriber<I, O> subscriber) {
        this.subscribe(subscriber, i -> i, i -> true);
    }

    /**
     * 确定了发送者的目标接收者是谁,默认只能publish给一个subscription
     * 通过when关联了发送者和接受者
     *
     * @param subscriber 订阅者
     * @param convert 处理前when的转换函数
     * @param whether whether
     */
    @Override
    public <M, O> void subscribe(
            Subscriber<M, O> subscriber, Operators.Map<I, M> convert, Operators.Whether<I> whether) {
        // 默认只能将数据发给一个subscriber
        this.whens.add(new When<>(this.streamId, subscriber, convert, whether, repo, messenger));
    }

    /**
     * publish到某subscriber
     * just,map,reduce,produce可以生成subscriber，这个是直接指定subscriber
     *
     * @param eventId 事件ID
     * @param subscriber 订阅者
     */
    @Override
    public <O> void subscribe(String eventId, Subscriber<I, O> subscriber) {
        this.subscribe(eventId, subscriber, i -> i, i -> true);
    }

    /**
     * 确定了发送者的目标接收者是谁,默认只能publish给一个subscription
     * 通过when关联了发送者和接受者
     *
     * @param eventId 事件ID
     * @param subscriber 订阅者
     * @param convert 处理前when的转换函数
     * @param whether whether
     */
    @Override
    public <M, O> void subscribe(String eventId, Subscriber<M, O> subscriber, Operators.Map<I, M> convert, Operators.Whether<I> whether) {
        this.whens.add(new When<>(this.streamId, eventId, subscriber, convert, whether, repo, messenger));
    }

    @Override
    public String getStreamId() {
        return this.streamId;
    }

    /**
     * 通过边的ID获取下一个节点的block，人工节点阻塞在边上
     *
     * @param eventId 事件ID
     * @return block
     */
    public Blocks.Block<I> getBlock(String eventId) {
        ArrayDeque<Subscriber<?, ?>> nodesQueue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        for (Subscription<?, ?> s : this.getSubscriptions()) {
            nodesQueue.addLast(s.getTo());
            visited.add(s.getTo().getId());
            if (s.getId().equals(eventId)) {
                return (Blocks.Block<I>) s.getTo().block();
            }
        }

        while (!nodesQueue.isEmpty()) {
            Node<?, ?> curNode = (Node<?, ?>) nodesQueue.removeFirst();
            for (Subscription<?, ?> s : curNode.getSubscriptions()) {
                if (!visited.contains(s.getTo().getId())) {
                    nodesQueue.offer(s.getTo());
                    visited.add(s.getTo().getId());
                }
                if (s.getId().equals(eventId)) {
                    return (Blocks.Block<I>) s.getTo().block();
                }
            }
        }
        throw new WaterflowException(FLOW_ENGINE_INVALID_MANUAL_TASK);
    }

    // 开始节点无需处理直接标记结束
    private List<FlowContext<I>> startNodeMarkAsHandled(List<FlowContext<I>> pre, FlowTrace trace) {
        String fromBatchId = UUIDUtil.uuid();
        String toBatchId = UUIDUtil.uuid();
        trace.setStartNode(this.getId());
        trace.setStreamId(this.streamId);
        trace.setStatus(FlowTraceStatus.RUNNING);
        pre.forEach(c -> {
            trace.getContextPool().add(c.getId());
            c.batchId(fromBatchId);
            c.toBatch(toBatchId);
            c.setStatus(FlowNodeStatus.ARCHIVED);
        });
        List<FlowContext<I>> after = pre.stream().map(c -> {
            FlowContext<I> context = c.generate(c.getData(), c.getPosition()).batchId(toBatchId);
            trace.getContextPool().add(context.getId());
            return context;
        }).collect(Collectors.toList());
        repo.save(trace, pre.get(0));
        repo.save(after);
        repo.save(pre);
        GlobalFileData.remove(pre.stream().map(IdGenerator::getId).collect(Collectors.toList()));
        return after;
    }

}
