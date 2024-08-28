/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.config;

/**
 * 向量索引类型。
 *
 * @since 2024-05-09
 */
public enum IndexType {
    FLAT,
    IVF_FLAT,
    IVF_PQ,
    HNSW,
    DISKANN,
}
