/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.pattern;

/**
 * 表示检索后置处理器算子接口。
 *
 * @param <D> 表示待处理数据的泛型。
 * @author 易文渊
 * @since 2024-08-12
 */
@FunctionalInterface
public interface PostProcessor<D> extends Pattern<D, D> {
    /**
     * 对检索结果进行后处理。
     *
     * @param data 表示输入数据的 {@link D}。
     * @return 表示处理后数据的 {@link D>}。
     */
    D process(D data);

    @Override
    default D invoke(D data) {
        return this.process(data);
    }
}