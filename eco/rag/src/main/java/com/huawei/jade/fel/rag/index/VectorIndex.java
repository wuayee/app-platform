/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.index;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.jade.fel.core.retriever.Indexer;
import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.common.EmbeddingModel;
import com.huawei.jade.fel.rag.store.VectorStore;
import com.huawei.jade.fel.rag.store.query.Expression;
import com.huawei.jade.fel.rag.store.query.VectorQuery;

import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 向量化索引。
 * <p>负责数据的向量化和存取。</p>
 *
 * @since 2024-05-07
 */
@Setter
public class VectorIndex implements Indexer<List<Chunk>> {
    private static final String EMBEDDING = "embedding";

    private VectorStore vs;
    private EmbeddingModel model;

    /**
     * 根据传入的向量数据库和向量化模型构造 {@link VectorIndex} 的实例。
     *
     * @param vs 表示向量数据库的 {@link VectorStore}。
     * @param model 表示向量化模型的 {@link EmbeddingModel}。
     */
    public VectorIndex(@Nonnull VectorStore vs, @Nonnull EmbeddingModel model) {
        this.vs = vs;
        this.model = model;
    }

    /**
     * 实现 {@link Indexer} 的调用接口。
     *
     * @param input 表示要进行向量化并存储的数据的 {@link List}{@code <}{@link Chunk}{@code >}。
     */
    @Override
    public void process(List<Chunk> input) {
        addChunks(input);
    }

    private void addChunks(List<Chunk> chunks) {
        List<Chunk> indexes = chunks.stream()
                        .map(chunk -> chunk.addMetadata(EMBEDDING, model.invoke(chunk.getContent())))
                        .collect(Collectors.toList());
        vs.put(indexes, vs.getConfig());
    }

    /**
     * 根据传入的字符串和搜索条件，在向量数据库查询最相似的topK个{@link Chunk}。
     *
     * @param queryStr 表示要搜索字符串的 {@link String}。
     * @param topK 表示最终结果最多包含个数的 {@code int}。
     * @param expr 表示搜索向量数据库时的其他搜索条件的 {@link Expression}。
     * @return 返回最相似的topK个{@link Chunk}。
     */
    public List<Chunk> searchChunks(String queryStr, int topK, Expression expr) {
        VectorQuery query = VectorQuery.builder()
                .topK(topK)
                .expr(null)
                .embedding(model.invoke(queryStr))
                .build();

        return (List<Chunk>) vs.get(query, vs.getConfig());
    }
}
