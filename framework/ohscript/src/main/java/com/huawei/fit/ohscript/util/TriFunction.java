/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.util;

import com.huawei.fit.ohscript.script.errors.OhPanic;

/**
 * 三个入参一个返回值的函数式接口
 *
 * @param <T> 入参1类型
 * @param <U> 入参2类型
 * @param <V> 入参3类型
 * @param <R> 返回值类型
 * @since 1.0
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    /**
     * 执行函数式接口的功能
     *
     * @param t 入参1
     * @param u 入参2
     * @param v 入参3
     * @return 返回值
     * @throws OhPanic 执行过程中可能抛出的异常
     */
    R apply(T t, U u, V v) throws OhPanic;
}
