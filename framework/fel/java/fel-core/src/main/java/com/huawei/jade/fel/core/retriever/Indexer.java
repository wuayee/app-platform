/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.retriever;

/**
 * 索引算子，用于 RAG 流程中的知识入库。
 *
 * @param <I> 表示索引算子的入参类型。
 * @author 刘信宏
 * @since 2024-04-28
 */
@FunctionalInterface
public interface Indexer<I> {
    /**
     * 索引算子的处理方法。
     *
     * @param input 表示索引算子的输入数据的 {@link I}。
     */
    void process(I input);
}
