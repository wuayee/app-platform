/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators;

/**
 * AI 流程可执行的数据处理接口。
 *
 * @param <I> 表示输入数据类型。
 * @param <O> 表示执行后输出数据的类型。
 * @author 刘信宏
 * @since 2024-04-22
 */
public interface AiRunnable<I, O> {
    /**
     * 处理方法。
     *
     * @param arg 表示输入參數的 {@link CustomState}{@code <}{@link I}{@code >}。
     * @return 表示输出数据的 {@link O}。
     */
    O invoke(CustomState<I> arg);
}
