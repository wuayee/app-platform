/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.retriever;

import modelengine.fel.core.Pattern;

/**
 * 检索算子。
 *
 * @param <I> 表示输入参数的类型。
 * @author 刘信宏
 * @since 2024-04-28
 */
@FunctionalInterface
public interface Retriever<I, O> extends Pattern<I, O> {}
