/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.pattern;

import com.huawei.jade.fel.core.document.Document;

import java.util.List;

/**
 * 表示数据源的实体。
 *
 * @param <I> 表示输入参数的泛型。
 * @author 易文渊
 * @since 2024-08-06
 */
@FunctionalInterface
public interface Source<I> extends Pattern<I, List<Document>> {
    /**
     * 根据输入参数加载文档。
     *
     * @param input 表示输入参数的 {@link I}。
     * @return 表示加载文档列表的 {@link List}{@code <}{@link Document}{@code >}。
     */
    List<Document> load(I input);

    @Override
    default List<Document> invoke(I input) {
        return this.load(input);
    }
}