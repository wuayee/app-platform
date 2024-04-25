/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.states;

import com.huawei.fit.waterflow.domain.flow.ProcessFlow;
import com.huawei.fit.waterflow.domain.utils.SleepUtil;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * 数据前置的state节点
 *
 * @param <O> 出参数据类型
 * @param <D> 原始数据类型
 * @param <I> 入参数据类型
 * @since 1.0
 */
public class DataState<O, D, I> extends DataStart<O, D, I> {
    public DataState(State<O, D, I, ProcessFlow<D>> state, DataStart<D, D, D> start) {
        super(state);
        this.start = start;
    }

    /**
     * 完成流程定义，并发射一个数据，提供一个callback用于接受数据
     *
     * @param callback 回调
     */
    public void offer(Consumer<O> callback) {
        ((State<O, D, I, ProcessFlow<D>>) this.state).close(r -> callback.accept(r.get().getData()));
        this.start.offer();
    }

    /**
     * 完成流程定义，并发射一个数据
     */
    public void offer() {
        ((State<O, D, I, ProcessFlow<D>>) this.state).close();
        this.start.offer();
    }

    /**
     * 启动流程并同步等待结果
     *
     * @return 流程处理的最终结果
     */
    public O invoke() {
        AtomicReference<O> result = new AtomicReference<>();
        ((State<O, D, I, ProcessFlow<D>>) this.state).close(r -> result.set(r.get().getData()));
        this.start.offer();
        SleepUtil.waitUntil(() -> result.get() != null, 1000);
        return result.get();
    }

    /**
     * 跳转到指定节点，使用节点的别名id来标识一个节点
     * <p>
     * 类似于goto，通常在conditions的分支中使用，用于形成循环
     * </p>
     * <p>
     * 包装了{@link State#to(String)}的实现
     * </p>
     *
     * @param id 节点的别名id，通常使用 {@link State#id(String)} 指定
     */
    public void to(String id) {
        ObjectUtils.<State>cast(this.state).to(id);
    }

    /**
     * 跳转到指定节点
     * <p>
     * 类似于goto，通常在conditions的分支中使用，用于形成循环
     * </p>
     * <p>
     * 包装了{@link State#to(State)}的实现
     * </p>
     *
     * @param state 指定的节点
     */
    public void to(DataState<?, D, O> state) {
        ((State<O, D, I, ProcessFlow<D>>) this.state).to((State<?, D, O, ProcessFlow<D>>) state.state);
    }
}