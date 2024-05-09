/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.engine.operators.models;

import com.huawei.jade.fel.chat.ChatOptions;
import com.huawei.jade.fel.engine.operators.AiRunnable;

/**
 * 模型算子的基类。
 *
 * @param <I> 表示模型算子的输入类型。
 * @param <O> 表示模型算子的输出类型。
 * @author 刘信宏
 * @since 2024-04-16
 */
public interface Model<I, O> extends AiRunnable<I, O> {
    /**
     * 绑定模型超参数。
     *
     * @param options 表示模型超参数的 {@link ChatOptions}。
     * @return 表示绑定了超参数的 {@link Model}{@code <}{@link I}{@code , }{@link O}{@code >}。
     */
    Model<I, O> bind(ChatOptions options);
}
