/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core;

/**
 * 委托单元。
 *
 * @param <I> 表示输入数据类型。
 * @param <O> 表示输出数据类型。
 * @author 刘信宏
 * @since 2024-06-11
 */
public interface Pattern<I, O> {
    /**
     * 处理方法。
     *
     * @param input 表示输入数据的 {@link I}。
     * @return 表示输出数据的 {@link O}。
     */
    O invoke(I input);
}
