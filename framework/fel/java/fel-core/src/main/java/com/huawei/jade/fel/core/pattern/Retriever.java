/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.pattern;

import java.util.List;

/**
 * 表示检索算子。
 *
 * @param <I> 表示输入参数的类型。
 * @author 刘信宏
 * @since 2024-04-28
 */
@FunctionalInterface
public interface Retriever<I, O extends Measurable> extends Pattern<I, List<O>> {
    /**
     * 根据用户输入进行检索。
     *
     * @param query 表示用户输入的 {@link I}。
     * @return 返回可量化数据的 {@link List}{@code <}{@link O}{@code >}。
     */
    List<O> retrieve(I query);

    @Override
    default List<O> invoke(I query) {
        return this.retrieve(query);
    }
}
