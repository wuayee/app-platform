/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.callbacks;

import com.huawei.fit.waterflow.domain.stream.reactive.Callback;

import java.util.List;

/**
 * ToCallback表示对 {@link Callback} 的实现类
 *
 * @author 李哲峰
 * @since 1.0
 */
public class ToCallback<O> implements Callback<O> {
    private final List<O> products;

    public ToCallback(List<O> products) {
        this.products = products;
    }

    /**
     * 获取所有待回调发送的数据
     *
     * @return list类型的数据
     */
    @Override
    public List<O> getAll() {
        return this.products;
    }

    /**
     * get
     *
     * @return O
     */
    @Override
    public O get() {
        return this.products.get(0);
    }
}
