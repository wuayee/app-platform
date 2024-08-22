/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.retrieve;

import modelengine.fel.core.retriever.Retriever;
import modelengine.fel.rag.Chunk;
import modelengine.fel.rag.Chunks;
import modelengine.fel.rag.index.VectorIndex;
import modelengine.fel.rag.rerank.ModelRerank;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 检索后利用模型进行重排序。
 *
 * @since 2024-05-07
 */
public class ModelRerankRetriever implements Retriever<String, String> {
    private VectorIndex indexer;
    private ModelRerank reranker;
    private int topK;

    /**
     * 根据向量化索引、模型重排序实例以及topK构建 {@link ModelRerankRetriever} 实例。
     *
     * @param indexer 表示向量化索引的 {@link VectorIndex}。
     * @param reranker 表示模型重排序实例的 {@link ModelRerank}。
     * @param topK 表示要保留的检索结果的个数，类型为 {@code int}。
     */
    public ModelRerankRetriever(VectorIndex indexer, ModelRerank reranker, int topK) {
        this.topK = topK;
        this.indexer = indexer;
        this.reranker = reranker;
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

        List<Chunk> reranked = reranker.invoke(question, Chunks.from(searched)).getChunks();

        return Chunks.from(reranked.stream()
                        .limit(topK)
                        .collect(Collectors.toList())).text();
    }
}
