/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.streams.callbacks;

import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream;

import java.util.List;

/**
 * ToCallback表示对 {@link FitStream.Callback} 的实现类
 *
 * @author l00862071
 * @since 2023-12-12
 */
public class ToCallback<O> implements FitStream.Callback<O> {
    private final List<O> products;

    public ToCallback(List<O> products) {
        this.products = products;
    }

    public List<O> getAll() {
        return this.products;
    }

    /**
     * get
     *
     * @return O
     */
    public O get() {
        return this.products.get(0);
    }
}
