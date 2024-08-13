/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.flowable.util.counter;

/**
 * 表示 {@link Counter} 数值变化的观察者。
 *
 * @author 何天放
 * @since 2024-02-20
 */
@FunctionalInterface
public interface CounterValueChangedObserver {
    /**
     * Counter 中计数值发生变化时所执行操作。
     *
     * @param counter 表示计数值发生变化的 {@link Counter}。
     * @param pre 表示变化前计数值的 {@code long}。
     * @param next 表示变化后计数值的 {@code long}。
     */
    void onValueChanged(Counter counter, long pre, long next);
}
