/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.streams;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_ENGINE_INVALID_MANUAL_TASK;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_ENGINE_INVALID_NODE_ID;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.utils.UUIDUtil;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowTrace;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowTrans;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowTraceStatus;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.ParallelMode;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Processor;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Publisher;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Subscriber;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Subscription;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.FlatMap;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Just;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Map;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Produce;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Reduce;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Whether;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.Blocks.Block;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.ConditionsNode;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.JoinNode;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.Node;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.ParallelNode;
import com.huawei.fitframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        this.streamId = streamId != null && !"".equals(streamId.trim()) ? streamId : this.id;
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
     * @param processor just处理器
     * @param convert convert
     * @param whether whether
     * @return 新的条件processor
     */
    @Override
    public <O> Processor<O, O> conditions(Just<FlowContext<O>> processor, Map<I, O> convert, Whether<I> whether) {
        ConditionsNode<O> node = new ConditionsNode<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    /**
     * 数据发送给平行（广播式）processor
     *
     * @param processor just处理器
     * @param mode either还是all
     * @param convert convert
     * @param whether whether
     * @return 新的条件processor
     */
    @Override
    public <O> Processor<O, O> parallel(Just<FlowContext<O>> processor, ParallelMode mode, Map<I, O> convert,
            Whether<I> whether) {
        ParallelNode<O> node = new ParallelNode<>(this.getStreamId(), processor, mode, repo, messenger, locks);
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
    public <M, O> Processor<M, O> join(Reduce<FlowContext<M>, O> processor, Map<I, M> convert, Whether<I> whether) {
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
    public <O> Processor<O, O> just(Just<FlowContext<O>> processor, Map<I, O> convert, Whether<I> whether) {
        // just的实现就是一个返回自己的map
        Node<O, O> node = new Node<>(this.getStreamId(), (Map<FlowContext<O>, O>) i -> {
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
    public <M, O> Processor<M, O> map(Map<FlowContext<M>, O> processor, Map<I, M> convert, Whether<I> whether) {
        Node<M, O> node = new Node<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    @Override
    public <M, O> Processor<M, O> flatMap(FlatMap<FlowContext<M>, O> processor, Map<I, M> convert, Whether<I> whether) {
        Node<M, O> node = new Node<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, whether);
        return node;
    }

    /**
     * 接收者是n:1数据处理：进n条A数据，返回1条B数据
     *
     * @param processor 处理函数
     * @param convert 处理前when的转换函数
     * @param whether whether
     * @return 返回一个node，这个node是接收者，同时可以继续发送数据
     */
    @Override
    public <M, O> Processor<M, O> reduce(Reduce<FlowContext<M>, O> processor, Map<I, M> convert, Whether<I> whether) {
        Node<M, O> node = new Node<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, i -> true);
        return node;
    }

    /**
     * 接受者是m:n数据处理: 进m条A数据，返回n条B数据
     *
     * @param processor 处理函数
     * @param convert 处理前when的转换函数
     * @param whether whether
     * @return 返回一个node，这个node是接收者，同时可以继续发送数据
     */
    @Override
    public <M, O> Processor<M, O> produce(Produce<FlowContext<M>, O> processor, Map<I, M> convert, Whether<I> whether) {
        Node<M, O> node = new Node<>(this.getStreamId(), processor, repo, messenger, locks);
        this.subscribe(node, convert, i -> true);
        return node;
    }

    /**
     * offer
     *
     * @param data data
     * @param trans trans
     * @return FlowOfferId
     */
    public FlowOfferId offer(I data, FlowTrans trans) {
        I[] array = (I[]) new Object[1];
        array[0] = data;
        return this.offer(array, trans);
    }

    /**
     * offer
     *
     * @param data data data
     * @param trans trans trans
     * @return FlowOfferId
     */
    public FlowOfferId offer(I[] data, FlowTrans trans) {
        FlowTrace trace = new FlowTrace();
        repo.getTraceOwnerService().own(trace.getId(), trans.getId());
        Set<String> traceId = new HashSet<>();
        traceId.add(trace.getId());
        List<FlowContext<I>> contexts = Arrays.stream(data)
                .map(d -> new FlowContext<>(this.getStreamId(), this.getId(), d, traceId, this.getId()).inFlowTrans(
                        trans))
                .collect(Collectors.toList());
        this.offer(startNodeMarkAsHandled(contexts, trace));
        return new FlowOfferId(trans, trace.getId());
    }

    /**
     * publish单条数据
     * 外部使用：controller中的start flow有使用，且返回给了前端
     * 内部使用：全部是单元测试在使用
     *
     * @param data 单条数据
     * @return 当前提交的流程实例相关ID
     */
    @Override
    public FlowOfferId offer(I data) {
        return this.offer(data, new FlowTrans());
    }

    /**
     * publish 多条数据
     * 外部使用：暂无
     * 内部使用：全部是单元测试在使用
     *
     * @param data 多条数据
     * @return 当前提交的流程实例相关ID
     */
    @Override
    public FlowOfferId offer(I... data) {
        return this.offer(data, new FlowTrans());
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
        if (contexts.stream().map(c -> c.getTrans().getId()).distinct().count() != 1) {
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
                w -> w.cache(contexts.stream().filter(c -> w.getWhether().is(c)).collect(Collectors.toList())));
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
    public <M, O> void subscribe(Subscriber<M, O> subscriber, Map<I, M> convert, Whether<I> whether) {
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
    public <M, O> void subscribe(String eventId, Subscriber<M, O> subscriber, Map<I, M> convert, Whether<I> whether) {
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
    public Block<I> getBlock(String eventId) {
        ArrayDeque<Subscriber<?, ?>> nodesQueue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        for (Subscription<?, ?> s : this.getSubscriptions()) {
            nodesQueue.addLast(s.getTo());
            visited.add(s.getTo().getId());
            if (s.getId().equals(eventId)) {
                return (Block<I>) s.getTo().block();
            }
        }

        while (!nodesQueue.isEmpty()) {
            Subscriber<?, ?> cur = nodesQueue.removeFirst();
            if (!(cur instanceof Node<?, ?>)) {
                continue;
            }
            Node<?, ?> curNode = (Node<?, ?>) cur;
            for (Subscription<?, ?> s : curNode.getSubscriptions()) {
                if (!visited.contains(s.getTo().getId())) {
                    nodesQueue.offer(s.getTo());
                    visited.add(s.getTo().getId());
                }
                if (s.getId().equals(eventId)) {
                    return (Block<I>) s.getTo().block();
                }
            }
        }
        throw new JobberException(FLOW_ENGINE_INVALID_MANUAL_TASK);
    }

    /**
     * 通过订阅节点Id查找订阅节点
     *
     * @param nodeId 节点Id
     * @return 订阅节点
     */
    public To<I, Object> getSubscriber(String nodeId) {
        return (To<I, Object>) Optional.ofNullable(findNode(this, nodeId))
                .orElseThrow(() -> new JobberException(FLOW_ENGINE_INVALID_NODE_ID, nodeId));
    }

    /**
     * findNodeFromFlow
     *
     * @param from from
     * @param nodeMetaId nodeMetaId
     * @return Node<FlowData, FlowData>
     */
    public Node<FlowData, FlowData> findNodeFromFlow(From<FlowData> from, String nodeMetaId) {
        return (Node<FlowData, FlowData>) Optional.ofNullable(findNode(this, nodeMetaId)).get();
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
            FlowContext<I> context = c.generate(c.getData(), c.getPosition(), LocalDateTime.now()).batchId(toBatchId);
            trace.getContextPool().add(context.getId());
            return context;
        }).collect(Collectors.toList());
        repo.save(trace, pre.get(0));
        repo.save(after);
        repo.save(pre);
        return after;
    }

    /**
     * findNode
     *
     * @param from from
     * @param nodeMetaId nodeMetaId
     * @return To<?, ?> To object
     */
    private static To<?, ?> findNode(From<?> from, String nodeMetaId) {
        ArrayDeque<Subscriber<?, ?>> nodesQueue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        for (Subscription<?, ?> s : from.getSubscriptions()) {
            Subscriber<?, Object> to = s.getTo();
            nodesQueue.addLast(to);
            visited.add(to.getId());
            if (to.getId().equals(nodeMetaId)) {
                return (To<?, ?>) to;
            }
        }

        while (!nodesQueue.isEmpty()) {
            Subscriber<?, ?> cur = nodesQueue.removeFirst();
            if (!(cur instanceof Node<?, ?>)) {
                continue;
            }
            Node<?, ?> curNode = (Node<?, ?>) cur;
            for (Subscription<?, ?> s : curNode.getSubscriptions()) {
                Subscriber<?, Object> to = s.getTo();
                if (!visited.contains(to.getId())) {
                    nodesQueue.offer(to);
                    visited.add(to.getId());
                }
                if (to.getId().equals(nodeMetaId)) {
                    return (To<?, ?>) to;
                }
            }
        }
        return null;
    }
}
