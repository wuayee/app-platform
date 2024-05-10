/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.connector.schema;

/**
 * 向量数据库字段的数据类型。
 *
 * @since 2024-05-07
 */
public enum VectorFieldDataType {
    VARCHAR, // variable-length strings with a specified maximum length
    JSON,

    FLOATVECTOR,
}
