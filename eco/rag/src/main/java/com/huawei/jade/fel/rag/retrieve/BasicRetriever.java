/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.retrieve;

import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.common.Chunks;
import com.huawei.jade.fel.rag.index.VectorIndex;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 简单Retriever实现。
 *
 * @since 2024-05-07
 */
public class BasicRetriever implements Retriever<String, String> {
    private VectorIndex indexer;
    private int topK;

    /**
     * 根据向量索引和topK构造 {@link BasicRetriever} 实例。
     *
     * @param indexer 表示向量化索引的 {@link VectorIndex}。
     * @param topK 表示最多保留topK个检索结果的 {@code int}。
     */
    public BasicRetriever(VectorIndex indexer, int topK) {
        this.indexer = indexer;
        this.topK = topK;
    }

    /**
     * 根据传入的查询进行检索，保留topK个内容。
     *
     * @param question 表示要检索的问题的 {@link String}。
     * @return 返回检索到的数据。
     */
    @Override
    public String invoke(String question) {
        List<Chunk> searched = indexer.searchChunks(question, topK, null);

        return Chunks.from(
                searched.stream()
                        .limit(topK)
                        .collect(Collectors.toList())).text();
    }
}
