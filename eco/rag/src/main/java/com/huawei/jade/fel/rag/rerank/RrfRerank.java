/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.rerank;

import com.huawei.jade.fel.core.retriever.Indexer;
import com.huawei.jade.fel.rag.Chunk;
import com.huawei.jade.fel.rag.Chunks;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 倒数排名融合的重排序
 * <p>用于将来自不同检索结果的数据序列进行重新排序。</p>
 *
 * @since 2024-05-07
 */
public class RrfRerank extends HybridRerank {
    private static final int DEFAULT_FACTOR = 60;

    private int factor;

    /**
     *  根据默认的倒数系数(factor=60) 构造 {@link RrfRerank} 实例。
     */
    public RrfRerank() {
        this(DEFAULT_FACTOR);
    }

    /**
     * 根据传入的倒数系数构造 {@link RrfRerank} 实例。
     *
     * @param factor 表示倒数系数的 {@code int}。
     */
    public RrfRerank(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("factor must be non-negative");
        }
        this.factor = factor;
    }

    /**
     * 将检索到的数据按照Reciprocal Rank Fusion算法进行重排序。
     *
     * @param data data 表示经过 {@link Indexer}
     *             检索到的数据的 {@link List}{@code <}{@link List}{@code <}{@link Chunk}{@code >}{@code >}。
     * @return 返回重排序后的结果。
     */
    @Override
    public Chunks invoke(List<Chunks> data) {
        Map<Chunk, Double> res = new HashMap<>();
        int limit = 0;

        for (Chunks chunks : data) {
            limit = Math.max(limit, chunks.getChunks().size());
            for (int i = 0; i < chunks.getChunks().size(); i++) {
                Chunk chunk = chunks.getChunks().get(i);
                double score = (double) 1 / (i + factor);
                res.compute(chunk, (k, v) -> (v == null) ? score : v + score);
            }
        }

        return Chunks.from(res.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));
    }
}
