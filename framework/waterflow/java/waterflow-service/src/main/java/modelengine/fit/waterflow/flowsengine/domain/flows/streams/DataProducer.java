/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.ArrayInvoke;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Invoke;

/**
 * 数据生产节点
 *
 * @param <T> 待生产数据类型
 * @author 高诗意
 * @since 2023/08/14
 */
public class DataProducer<T> extends IdGenerator implements FitStream.Source<T> {
    private final FlowContextRepo repo;

    private final FlowContextMessenger messenger;

    private final FlowLocks locks;

    /**
     * 数据出来后的第一个publisher
     */
    private FitStream.Publisher<T> from;

    /**
     * 单数据处理器
     */
    private Invoke<T> invoker = null;

    /**
     * 多数据处理器
     */
    private ArrayInvoke<T> arrayInvoker = null;

    public DataProducer(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        this.repo = repo;
        this.messenger = messenger;
        this.locks = locks;
    }

    private FitStream.Publisher<T> from() {
        this.invoker = null;
        this.arrayInvoker = null;
        return new From<>(repo, messenger, locks);
    }

    /**
     * 单数据构造
     *
     * @param invoker 单数据处理器
     * @return Publisher 单数据处理器
     */
    @Override
    public FitStream.Publisher<T> mono(Invoke<T> invoker) {
        this.from = this.from();
        this.invoker = invoker;
        return this.from;
    }

    /**
     * 多数据构造
     *
     * @param invoker 多数据处理器
     * @return Publisher
     */
    @Override
    public FitStream.Publisher<T> flux(ArrayInvoke<T> invoker) {
        this.from = this.from();
        this.arrayInvoker = invoker;
        return this.from;
    }

    /**
     * 开始生产数据，并发给相应的publisher
     */
    @Override
    public void produce() {
        if (invoker != null) {
            this.from.offer(invoker.invoke());
        }
        if (arrayInvoker != null) {
            this.from.offer(arrayInvoker.invoke());
        }
    }
}
