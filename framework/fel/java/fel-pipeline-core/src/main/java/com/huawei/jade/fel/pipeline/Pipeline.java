/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline;

import java.util.function.Function;

/**
 * 流水线标准接口定义。
 *
 * @param <I> 表示流水线输入参数类型的 {@link I}。
 * @param <O> 表示流水线输出参数类型的 {@link O}。
 * @author 易文渊
 * @since 2024-06-07
 */
public interface Pipeline<I, O> extends Function<I, O> {}