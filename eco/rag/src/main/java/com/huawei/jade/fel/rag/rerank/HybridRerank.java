/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.rerank;

import com.huawei.jade.fel.core.retriever.Indexer;
import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.Chunks;

import java.util.List;

/**
 * 混合重排序基类，将来自多个检索源的数据进行重排序。
 *
 * @since 2024-05-07
 */
public abstract class HybridRerank implements Rerank<List<Chunks>, Chunks> {
    /**
     * 对检索到的数据进行重排序。
     *
     * @param data 表示经过多个 {@link Indexer}
     *             检索到数据的 {@link List}{@code <}{@link List}{@code <}{@link Chunk}{@code >}{@code >}。
     * @return 返回重排序后的数据。
     */
    @Override
    public Chunks invoke(List<Chunks> data) {
        return new Chunks();
    }

    /**
     * 根据查询信息内容对检索到的数据进行重排序。
     *
     * @param query 表示来自 Retriever 的查询信息的 {@link String}。
     * @param data 表示经过 {@link Indexer}
     *             检索到数据的 {@link List}{@code <}{@link List}{@code <}{@link Chunk}{@code >}{@code >}。
     * @return 返回重排序后的数据。
     */
    @Override
    public final Chunks invoke(String query, List<Chunks> data) {
        return new Chunks();
    }
}
