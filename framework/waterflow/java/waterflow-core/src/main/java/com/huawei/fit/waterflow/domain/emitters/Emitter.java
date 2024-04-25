/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.emitters;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.stream.nodes.Retryable;

import java.util.List;

/**
 * 除了waterflow自身的驱动，waterflow允许外部数据实现InterStream接口
 * 给waterflow响应式灌入外部数据
 * huizi 2024
 *
 * @since 1.0
 */
public interface Emitter<D, T> {
    /**
     * 注册监听
     *
     * @param listener 监听器
     */
    void register(EmitterListener<D, T> listener);

    /**
     * 发布一个数据，并制定session
     *
     * @param data 待发布的数据
     * @param token 指定的session
     */
    void emit(D data, T token);

    /**
     * 发布一个数据，不指定session
     *
     * @param data 待发布的数据
     */
    default void emit(D data) {
        emit(data, null);
    }

    /**
     * 发布一个异常
     *
     * @param throwable 待发布的异常
     * @param retryable retryable
     * @param contexts contexts
     */
    default void error(Throwable throwable, Retryable<D> retryable, List<FlowContext<D>> contexts) {}

    /**
     * 将持有的数据逐个发布出去
     *
     * @param token 发布数据时归属的session
     */
    default void start(T token) {
    }
}
