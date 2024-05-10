/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.common;

import com.huawei.jade.fel.rag.store.config.MetricType;

import java.math.BigDecimal;

/**
 * 向量相似度度量的归一化工具类。
 *
 * @since 2024-05-07
 */
public class ScoreNormalizer {
    /**
     * 根据传入的得分和度量类型进行归一化。
     *
     * @param score 表示原始得分的 {@link Float}。
     * @param metricType 表示衡量标准的 {@link String}。
     * @param shouldNormalize 布尔值，表示是否归一化。
     * @return 返回处理后的结果。
     */
    public static Float process(Float score, MetricType metricType, boolean shouldNormalize) {
        if (!shouldNormalize && "L2".equals(metricType)) {
            return score;
        }

        BigDecimal result;
        switch (metricType) {
            case L2:
                result = BigDecimal.ONE.divide(
                        BigDecimal.ONE.add(BigDecimal.valueOf(score)), 5, BigDecimal.ROUND_HALF_UP);
                break;
            case IP:
                result = BigDecimal.valueOf(0.5).multiply(BigDecimal.ONE.add(BigDecimal.valueOf(Math.tanh(score))));
                break;
            case COSINE:
                result = BigDecimal.valueOf(0.5).multiply(BigDecimal.ONE.add(BigDecimal.valueOf(score)));
                break;
            default:
                result = BigDecimal.valueOf(score);
                break;
        }
        return result.floatValue();
    }
}