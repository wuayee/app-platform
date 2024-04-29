/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.formatters;

/**
 * 表示输出解析器的接口，包含语言模型的输出应该如何格式化的指令和解析为某个对象的方法。
 *
 * @param <O> 表示输出对象类型。
 * @author 易文渊
 * @see Formatter
 * @see Parser
 * @since 2024-04-28
 */
public interface OutputParser<O> extends Formatter, Parser<O> {}