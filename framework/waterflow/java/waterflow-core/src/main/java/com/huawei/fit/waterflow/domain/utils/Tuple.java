/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.utils;

/**
 * 二元组
 *
 * @param <F> 第一个参数类型
 * @param <S> 第二个参数类型
 * @since 1.0
 */
public class Tuple<F, S> {
    private final F first;

    private final S second;

    private Tuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * 构造一个Tuple
     *
     * @param first 参数1
     * @param second 参数2
     * @param <A> 参数1类型
     * @param <B> 参数2类型
     * @return 构造的Tuple
     */
    public static <A, B> Tuple from(A first, B second) {
        return new Tuple(first, second);
    }

    /**
     * 第一个参数
     *
     * @return 第一个参数
     */
    public F first() {
        return first;
    }

    /**
     * 第二个参数
     *
     * @return 第二个参数
     */
    public S second() {
        return second;
    }

    @Override
    public String toString() {
        return "key:" + first + ",value:" + second;
    }
}
