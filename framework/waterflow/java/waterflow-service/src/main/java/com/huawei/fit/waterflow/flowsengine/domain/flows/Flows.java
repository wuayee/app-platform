/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.DataProducer;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import com.huawei.fitframework.util.ObjectUtils;

import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于工程师编程交互的Flow API集合
 * 通过使用Flows里的Function++能力，可以代码简洁轻松创建非常复杂的流处理的函数链
 * 辉子 2019-10-31
 *
 * @author g00564732
 * @since 2023/08/14
 */
public final class Flows {
    /**
     * stream持久化接口
     */
    @Setter
    private static FlowContextRepo fitRepo;

    /**
     * 消息接口
     */
    @Setter
    private static FlowContextMessenger flowContextMessenger;

    /**
     * 流程锁
     */
    @Setter
    private static FlowLocks locks;

    static {
        setFitRepo(new FlowContextMemoRepo());
        setFlowContextMessenger(new FlowContextMemoMessenger());
        setLocks(new FlowLocksMemo());
    }

    /**
     * 先建立流程，然后通过offer灌入数据进行处理
     *
     * @param <D> 要处理的数据类型
     * @return 处理流头结点
     */
    public static <D> Activities.Start<D, D, ProcessFlow<D>> create() {
        ProcessFlow<D> flow = new ProcessFlow<>();
        return new Activities.Start<>(flow.start, flow);
    }

    /**
     * 先建立流程，然后通过offer灌入数据进行处理
     *
     * @param <D> 要处理的数据类型
     * @param repo 上下文处理类
     * @param messenger 事件发送类
     * @param locks 流程锁
     * @return 处理流头结点
     */
    public static <D> Activities.Start<D, D, ProcessFlow<D>> create(FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        ProcessFlow<D> flow = new ProcessFlow<>(repo, messenger, locks);
        return new Activities.Start<>(flow.start, flow);
    }

    /**
     * 基于单条数据建立流，然后通过offer开始处理数据
     *
     * @param invoker 生产数据函数
     * @param <D> 要生产的数据类型
     * @return 处理流头结点
     */
    public static <D> Activities.Start<D, D, ProduceFlow<D>> mono(Processors.Invoke<D> invoker) {
        ProduceFlow<D> flow = new ProduceFlow<>(invoker);
        return new Activities.Start<>(flow.start, flow);
    }

    /**
     * 基于LIST数据建立流，然后通过offer开始处理数据
     *
     * @param invoker 生产数据函数
     * @param <D> 要生产的数据类型
     * @return 处理流头结点
     */
    public static <D> Activities.Start<D, D, ProduceFlow<D>> flux(Processors.ArrayInvoke<D> invoker) {
        ProduceFlow<D> flow = new ProduceFlow<>(invoker);
        return new Activities.Start<>(flow.start, flow);
    }

    /**
     * FitStream外的一层flow wrapper
     * 通过flow将fit stream的所有流操作按照functional++的方式串联起来
     * 通过泛型推演让编程效率提高，并且代码更为整洁
     * 辉子 2019-10-31
     *
     * @param <D> stream其实数据类型，用于offer数据时限定，该类型会被第一处理函数推导出来
     */
    public abstract static class Flow<D> extends IdGenerator {
        /**
         * 每条流除了起始点和终结点，还有若干中间节点：processor
         */
        protected final List<FitStream.Processor<?, ?>> nodes = new ArrayList<>();

        /**
         * 每一条流都有一个起始点：publisher
         */
        protected FitStream.Publisher<D> start;

        /**
         * 每一条流都有一个终结点：subscriber
         */
        protected FitStream.Subscriber end;

        /**
         * 可能存在的数据生产器
         */
        protected FitStream.Source<D> source;

        private final Map<String, Activities.Activity> tagNodes = new HashMap<>();

        /**
         * end
         *
         * @return FitStream.Publisher
         */
        public FitStream.Subscriber end() {
            return this.end;
        }

        /**
         * start
         *
         * @return FitStream.Publisher
         */
        public FitStream.Publisher<D> start() {
            return this.start;
        }

        /**
         * tagNode
         *
         * @param id id
         * @param activity activity
         */
        public void tagNode(String id, Activities.Activity activity) {
            this.tagNodes.put(id, activity);
        }

        /**
         * 从一个named节点侦听一个外部数据源，侦听后将得到外部数据源的emit数据
         *
         * @param id 节点id
         * @param publisher 外部数据源
         */
        public void offer(String id, InterStream publisher) {
            ObjectUtils.<Activities.State>cast(this.tagNodes.get(id)).offer(publisher);
        }

        /**
         * 传入单条数据进入stream处理
         *
         * @param data 待处理的数据
         * @return 流程实例相关ID
         */
        public FlowOfferId offer(D data) {
            return this.start.offer(data);
        }

        /**
         * offer
         *
         * @param data data
         * @param token token
         * @return FlowOfferId
         */
        public FlowOfferId offer(D data, String token) {
            return this.start.offer(data, new FlowTrans(token));
        }

        /**
         * 传入多条数据处理【数组参数】
         *
         * @param data 待处理数据
         * @return 流程实例相关ID
         */
        public FlowOfferId offer(D[] data) {
            return this.start.offer(data);
        }

        /**
         * 传入多条数据处理【数组参数】
         *
         * @param data 待处理数据
         * @param token token
         * @return 流程实例相关ID
         */
        public FlowOfferId offer(D[] data, String token) {
            return this.start.offer(data, new FlowTrans(token));
        }

        /**
         * 从一个named特定节点注入一个数据
         *
         * @param id 节点id
         * @param data 需要注入的数据
         * @return 流程实例相关ID
         */
        public FlowOfferId offer(String id, Object data) {
            return ObjectUtils.<Activities.State>cast(this.tagNodes.get(id)).processor.offer(data);
        }

        /**
         * offer
         *
         * @param id id
         * @param data data
         * @param token token
         * @return FlowOfferId
         */
        public FlowOfferId offer(String id, Object data, String token) {
            return ObjectUtils.<Activities.State>cast(this.tagNodes.get(id)).processor.offer(data,
                    new FlowTrans(token));
        }

        /**
         * 从一个named特定节点注入一组数据
         *
         * @param id 节点id
         * @param data 需要注入的数据
         * @return 流程实例相关ID
         */
        public FlowOfferId offer(String id, Object[] data) {
            return ObjectUtils.<Activities.State>cast(this.tagNodes.get(id)).processor.offer(data);
        }
    }

    /**
     * 处理数据Flow
     * 用于先定义流程，再不停传入不同数据驱动stream往下走
     *
     * @param <D> 初始传入数据类型
     */
    public static class ProcessFlow<D> extends Flow<D> implements InterStreamHandler<D>, InterStream {
        /**
         * 流从起始节点开始
         */
        public ProcessFlow() {
            this(Flows.fitRepo, Flows.flowContextMessenger, Flows.locks);
        }

        /**
         * 流从起始节点开始
         *
         * @param repo 上下文持久化
         * @param messenger 上下文发送器
         * @param locks 流程锁
         */
        public ProcessFlow(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
            this.start = new From<>(repo, messenger, locks);
        }

        @Override
        public void handle(D data, String token) {
            this.offer(data, token);
        }

        @Override
        public void handle(D[] data, String token) {
            this.offer(data, token);
        }

        @Override
        public void register(InterStreamHandler handler) {
            this.end.register(handler);
        }

        @Override
        public void publish(Object data, String token) {
            this.end.publish(data, token);
        }

        @Override
        public void publish(Object[] data, String token) {
            this.end.publish(data, token);
        }
    }

    /**
     * 生产数据Flow
     * 数据不是从外界传入，而是通过一个invoker生产出来，生产出来后的返回值将成为stream的初始数据
     *
     * @param <D>函数返回值类型
     */
    public static class ProduceFlow<D> extends Flow<D> {
        /**
         * 单数据集生产
         *
         * @param invoker 单数据生产函数
         */
        public ProduceFlow(Processors.Invoke<D> invoker) {
            this();
            this.start = this.source.mono(invoker);
        }

        /**
         * 多数据集生产
         *
         * @param invoker 列表数据生产函数
         */
        public ProduceFlow(Processors.ArrayInvoke<D> invoker) {
            this();
            this.start = this.source.flux(invoker);
        }

        private ProduceFlow() {
            this.source = new DataProducer<>(Flows.fitRepo, Flows.flowContextMessenger, Flows.locks);
        }

        /**
         * 开始生产数据，并将生产后数据进入流处理
         * 调用invoker生产数据
         */
        public void offer() {
            this.source.produce();
        }
    }
}