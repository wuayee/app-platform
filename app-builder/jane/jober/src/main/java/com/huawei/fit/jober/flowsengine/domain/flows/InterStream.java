/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows;

/**
 * 除了waterflow自身的驱动，waterflow允许外部数据实现InterStream接口
 * 给waterflow响应式灌入外部数据
 *
 * @author x00576283
 * @since 2024/02/17
 */
public interface InterStream<T> {
    /**
     * 注册监听
     *
     * @param handler handler
     */
    void register(InterStreamHandler<T> handler);

    /**
     * 单个publish数据
     *
     * @param data data
     * @param id id
     */
    void publish(T data, String id);

    /**
     * 批量publish数据
     *
     * @param data data
     * @param id id
     */
    void publish(T[] data, String id);
}
