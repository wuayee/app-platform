/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.rerank;

import com.huawei.jade.fel.core.retriever.Indexer;
import com.huawei.jade.fel.rag.common.Chunk;
import com.huawei.jade.fel.rag.common.Chunks;
import com.huawei.jade.fel.rag.common.RequireType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对混合检索到的数据序列进行加权重排序。
 *
 * @since 2024-05-07
 */
public class WeightedRerank extends HybridRerank {
    private List<Double> weights = null;

    /**
     * 构造权重相等的 {@link WeightedRerank} 实例。
     */
    public WeightedRerank() {}

    /**
     * 根据传入的权重构造 {@link WeightedRerank} 实例。
     *
     * @param weights 表示各检索源的权重的  {@link List}{@code <}{@code int}{@code >}。
     */
    public WeightedRerank(List<Double> weights) {
        this.weights = weights;
    }

    /**
     * 将检索到的数据按照传入的权重法进行加权计算后重新排序。
     *
     * @param input data 表示经过 {@link Indexer}
     *              检索到的数据的  {@link List}{@code <}{@link List}{@code <}{@link Chunk}{@code >}{@code >}。
     * @return 排序后的数据序列。
     */
    @Override
    public Chunks invoke(List<Chunks> input) {
        if (weights != null && input.size() != weights.size()) {
            throw new IllegalArgumentException("Should specify all weights");
        }
        int limit = 0;

        Map<Chunk, Double> res = new HashMap<>();
        for (int i = 0; i < input.size(); i++) {
            Chunks chunks = input.get(i);
            limit = Math.max(limit, chunks.getChunks().size());
            Double weight = weights == null ? 1.0 : weights.get(i);
            for (Chunk chunk : chunks.getChunks()) {
                Double score = weight * RequireType.check(chunk.getMetadata().get("score"), Double.class);
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