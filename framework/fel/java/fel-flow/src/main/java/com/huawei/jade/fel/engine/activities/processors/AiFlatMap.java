/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.jade.fel.engine.activities.processors;

import com.huawei.jade.fel.engine.activities.AiDataStart;

/**
 * /**
 * 节点处理1个原材料到m个产品的过程
 *
 * @param <T> 原材料类型
 * @param <R> 产品类型
 * @author x00576283
 * @since 1.0
 */
@FunctionalInterface
public interface AiFlatMap<T, R> {
    /**
     * process
     *
     * @param input input
     * @return 用于发射的新数据
     */
    AiDataStart<R, R, ?> process(T input);
}
