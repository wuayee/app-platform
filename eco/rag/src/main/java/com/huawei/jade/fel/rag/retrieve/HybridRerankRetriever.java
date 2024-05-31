/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.retrieve;

import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.common.Chunks;
import com.huawei.jade.fel.rag.index.VectorIndex;
import com.huawei.jade.fel.rag.rerank.HybridRerank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 混合重排序的检索类实现。
 *
 * @since 2024-05-07
 */
public class HybridRerankRetriever implements Retriever<String, Chunks> {
    private List<VectorIndex> indexers;
    private HybridRerank reranker;
    private int topK;

    /**
     * 根据向量化索引列表、混合重排序器以及检索topK构建 {@link HybridRerankRetriever} 实例。
     *
     * @param indexers 表示向量化索引列表的  {@link List}{@code <}{@link VectorIndex}{@code >}。
     * @param reranker 表示混合重排序实例的 {@link HybridRerank}。
     * @param topK 表示要保留的检索结果的个数，类型为 {@code int}。
     */
    public HybridRerankRetriever(List<VectorIndex> indexers, HybridRerank reranker, int topK) {
        this.topK = topK;
        this.indexers = indexers;
        this.reranker = reranker;
    }

    /**
     * 根据传入的查询进行检索，保留topK个内容。
     *
     * @param question 表示要检索的问题的 {@link String}。
     * @return 返回检索到的数据。
     */
    @Override
    public Chunks invoke(String question) {
        List<Chunks> searched = new ArrayList<>();

        for (VectorIndex vi : indexers) {
            searched.add(Chunks.from(vi.searchChunks(question, topK, null)));
        }

        List<Chunk> reranked = reranker.invoke(searched).getChunks();

        return Chunks.from(reranked.stream()
                .limit(topK)
                .collect(Collectors.toList()));
    }
}