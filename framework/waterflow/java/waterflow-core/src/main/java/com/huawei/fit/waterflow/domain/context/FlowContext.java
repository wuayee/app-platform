/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context;

import com.huawei.fit.waterflow.domain.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.domain.utils.IdGenerator;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * stream里承载数据上下文
 *
 * @since 1.0
 */
public final class FlowContext<T> extends IdGenerator implements StateContext {
    /**
     * 通过from.offer(data)而不是.offer(context)发起的数据会新增一个trace，这个trace会延续到flow end
     */
    @Getter
    private final Set<String> traceId;

    /**
     * 数据发起的起始节点:上任何一个publisher都可以发起数据，不一定是stream的起始节点
     * 只要通过from.offer(data)而不是.offer(context)的都是数据发起节点
     */
    @Getter
    private final String rootId;

    /**
     * 得到所处stream id
     */
    @Getter
    private final String streamId;

    /**
     * 上下文里带的数据，这是关键属性
     */
    @Getter
    @Setter
    private T data;

    /**
     * 上下文当前所处的位置:那个处理节点上
     */
    @Getter
    private String position;

    /**
     * 如果是parallel节点，是否join了
     */
    @Getter
    private boolean joined;

    /**
     * 上下文当前的状态值
     */
    @Getter
    private FlowNodeStatus status = FlowNodeStatus.NEW;

    /**
     * 上下文所有的transaction
     * 当数据是一个数据的时候，这个数组在一个FlowTransId里，在reduce，produce时默认统一处理
     */
    @Getter
    @Setter
    private FlowSession session;

    /**
     * 上下文所处的平行节点id：如果该上下文在平行节点才有效
     */
    @Getter
    private String parallel;

    /**
     * 所处平行节点模式，是either还是all
     */
    @Getter
    private String parallelMode;

    /**
     * 来源于哪个context
     */
    @Getter
    @Setter
    private String previous;

    /**
     * context的生产批次ID
     */
    @Getter
    private String batchId;

    /**
     * 转向哪个context批次
     */
    @Getter
    private String toBatch;

    /**
     * 标记该context是否已经发出事件
     */
    @Getter
    @Setter
    private boolean sent;

    /**
     * 当前context创建时间
     */
    @Getter
    @Setter
    private LocalDateTime createAt;

    /**
     * 当前context更新时间
     */
    @Getter
    @Setter
    private LocalDateTime updateAt;

    /**
     * 当前context处理完成时间
     */
    @Getter
    @Setter
    private LocalDateTime archivedAt;

    @Getter
    @Setter
    private WindowToken windowToken = null;

    private Object keyBy = null;

    private boolean isAccumulator;

    public FlowContext(String streamId, String rootId, T data, Set<String> traceId, String position) {
        this(streamId, rootId, data, traceId, position, "", "");
    }

    public FlowContext(String streamId, String rootId, T data, Set<String> traceId, String position, String parallel,
            String parallelMode) {
        this.streamId = streamId;
        this.rootId = rootId;
        this.data = data;
        this.traceId = new HashSet<>();
        this.traceId.addAll(traceId);
        this.position = position;
        this.parallel = parallel;
        this.parallelMode = parallelMode;
        this.createAt = LocalDateTime.now();
        this.session = new FlowSession();
    }

    /**
     * 设置所在的位置，线或者节点的id ，链式操作
     *
     * @param position 所在的位置
     * @return context自身
     */
    public FlowContext<T> setPosition(String position) {
        this.position = position;
        return this;
    }

    /**
     * 设置节点状态，链式操作
     * <p>
     * 参考{@link FlowNodeStatus}
     * </p>
     *
     * @param status 节点状态
     * @return context自身
     */
    public FlowContext<T> setStatus(FlowNodeStatus status) {
        this.status = status;
        return this;
    }

    /**
     * 设置并行节点的id，链式操作
     *
     * @param parallel 并行节点的id
     * @return context自身
     */
    public FlowContext<T> setParallel(String parallel) {
        this.parallel = parallel;
        return this;
    }

    /**
     * 设置并行的模式，链式操作
     * <p>
     * 参考{@link com.huawei.fit.waterflow.domain.enums.ParallelMode}
     * </p>
     *
     * @param parallelMode 并行模式
     * @return context自身
     */
    public FlowContext<T> setParallelMode(String parallelMode) {
        this.parallelMode = parallelMode;
        return this;
    }

    /**
     * 设置批次id，链式操作
     *
     * @param batchId 批次id
     * @return context自身
     */
    public FlowContext<T> batchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    /**
     * 设置toBatch的id，链式操作
     *
     * @param toBatchId toBatch的id
     * @return context自身
     */
    public FlowContext<T> toBatch(String toBatchId) {
        this.toBatch = toBatchId;
        return this;
    }

    /**
     * inFlowTrans
     *
     * @param flowSession flowTrans
     * @return FlowContext<T>
     */
    public FlowContext<T> inFlowTrans(FlowSession flowSession) {
        this.session = flowSession;
        return this;
    }

    /**
     * generate是在map，reduce，produce的过程中把大多数上一个context的内容复制给下一个
     *
     * @param data 处理后的数据
     * @param position 处理后所处的节点
     * @param <R> 处理后数据类型
     * @return 新的上下文
     */
    public <R> FlowContext<R> generate(R data, String position) {
        FlowContext<R> context = new FlowContext<>(this.streamId, this.rootId, data, this.traceId, this.position,
                this.parallel, this.parallelMode);
        context.position = position;
        context.previous = this.id;
        context.session = this.session;
        context.windowToken = this.windowToken;
        context.keyBy = this.keyBy == null ? this.session.keyBy() : this.keyBy;
        return context;
    }

    /**
     * generate是在map，reduce，produce的过程中把大多数上一个context的内容复制给下一个
     *
     * @param dataList 处理后的数据
     * @param position 处理后所处的节点
     * @param <R> 处理后数据类型
     * @return 新的上下文
     */
    public <R> List<FlowContext<R>> generate(List<R> dataList, String position) {
        return dataList.stream().map(data -> this.generate(data, position)).collect(Collectors.toList());
    }

    /**
     * 用于when.convert数据时候的转换context，除了包裹的数据类型不一样，所有其他信息都一样
     *
     * @param <R> 转换后的数据类型
     * @param data 转换后的数据
     * @param id contextId
     * @return 转换后的context
     */
    public <R> FlowContext<R> convertData(R data, String id) {
        FlowContext<R> context = new FlowContext<>(this.streamId, this.rootId, data, this.traceId, this.position,
                this.parallel, this.parallelMode);
        context.previous = this.previous;
        context.status = this.status;
        context.id = id;
        context.batchId = this.batchId;
        context.toBatch = this.toBatch;
        context.createAt = this.createAt;
        context.updateAt = this.updateAt;
        context.archivedAt = this.archivedAt;
        context.session = this.session;
        context.windowToken = this.windowToken;
        context.keyBy = this.keyBy == null ? this.session.keyBy() : this.keyBy;
        return context;
    }

    /**
     * join节点，将context的状态设置为joined
     *
     * @param joined 是否join
     */
    public void join(boolean joined) {
        this.joined = joined;
    }

    @Override
    public <R> R getState(String key) {
        return this.session.getState(key);
    }

    @Override
    public void setState(String key, Object value) {
        this.session.setState(key, value);
    }

    /**
     * 将本context设置为accumulator
     */
    public void setAsAccumulator() {
        this.isAccumulator = true;
    }

    /**
     * 判定是否是accumulator
     *
     * @return true/false
     */
    public boolean isAccumulator() {
        return this.isAccumulator;
    }

    /**
     * 获取keyBy
     *
     * @return keyBy
     */
    public Object keyBy() {
        return this.keyBy;
    }

    /**
     * 设置keyBy的key，将会影响session的key
     *
     * @param key 目标key
     */
    public void setKeyBy(Object key) {
        this.keyBy = key;
        this.session.setKeyBy(key);
    }
}
