/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.query;

/**
 * 表达式类型。
 *
 * @since 2024-05-07
 */
public enum ExpressionType {
    AND,
    OR,
    IN,
    NIN,
    GTE,
    EQ,
    LT,
    LTE,
    GT,
    NE
}
