/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.util;

import java.io.Serializable;

/**
 * 表示一个键值对。
 *
 * @param <K> 表示键的类型。
 * @param <V> 表示值的类型。
 * @since 1.0
 */
public class Pair<K, V> implements Serializable {
    private static final long serialVersionUID = 758503529547984748L;

    private final K first;

    private final V second;

    /**
     * 使用键和值初始化 {@link Pair} 类的新实例。
     *
     * @param first 表示键的 {@link K}。
     * @param second 表示值的 {@link V}。
     */
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 获取键。
     *
     * @return 表示键的 {@link K}。
     */
    public K first() {
        return this.first;
    }

    /**
     * 获取值。
     *
     * @return 表示值的 {@link V}。
     */
    public V second() {
        return this.second;
    }

    @Override
    public String toString() {
        return "{\"first\": " + this.first + ", \"second\": " + this.second + '}';
    }
}
