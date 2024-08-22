/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.retriever;

import modelengine.fel.core.Pattern;

/**
 * 文本切分算子。
 *
 * @param <I> 表示文本切分算子的入参类型。
 * @param <O> 表示文本切分算子的出参类型。
 * @author 刘信宏
 * @since 2024-04-28
 */
@FunctionalInterface
public interface Splitter<I, O> extends Pattern<I, O> {
    /**
     * 文本切分。
     *
     * @param input 表示输入数据的 {@link I}。
     * @return 表示返回数据的 {@link O}。
     */
    O split(I input);

    @Override
    default O invoke(I input) {
        return this.split(input);
    }
}
