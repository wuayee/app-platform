/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.config;

/**
 * 评估标准类型
 *
 * @since 2024-05-09
 */
public enum MetricType {
    L2,
    IP,
    COSINE,
    HAMMING,
    JACCARD;
}
