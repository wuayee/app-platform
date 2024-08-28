/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.model;

import modelengine.fel.core.Pattern;

/**
 * 模型算子的基类。
 *
 * @param <I> 表示模型算子的输入类型。
 * @param <O> 表示模型算子的输出类型。
 * @author 刘信宏
 * @since 2024-06-11
 */
public interface Model<I, O> extends Pattern<I, O> {}
