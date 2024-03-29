/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.util;

/**
 * 为对象提供比较器。
 *
 * @param <T> 表示待比较对象的类型。
 * @author 梁济时 l00815032
 * @since 1.0
 */
@FunctionalInterface
public interface Equalizer<T> {
    /**
     * 比较两个对象是否包含相同的数据。
     *
     * @param t1 表示待比较的第一个对象。
     * @param t2 表示待比较的第二个对象。
     * @return 若两个对象包含相同的数据，则为 {@code true}；否则为 {@code false}。
     */
    boolean equals(T t1, T t2);
}