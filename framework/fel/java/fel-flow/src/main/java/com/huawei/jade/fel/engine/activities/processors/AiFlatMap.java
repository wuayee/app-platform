/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities.processors;

import com.huawei.jade.fel.engine.activities.AiDataStart;

/**
 * 将每个数据转换为一个数据流，并往下发射流转。
 *
 * @param <T> 表示输入数据类型。
 * @param <R> 表示数据流的数据类型。
 * @author 夏斐
 * @since 2024-05-18
 */
@FunctionalInterface
public interface AiFlatMap<T, R> {
    /**
     * process
     *
     * @param input 表示输入数据的 {@link T}。
     * @return 表示数据前置开始节点的 {@link AiDataStart}{@code <}{@link R}{@code , }{@link R}{@code , ?>}。
     */
    AiDataStart<R, R, ?> process(T input);
}
