/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream;

/**
 * 新数据发射器
 *
 * @param <T> 新数据类型
 * @since 1.0
 */
public interface Collector<T> {
    /**
     * 发射一条新数据
     *
     * @param data 待发射的数据
     */
    void collect(T data);
}
