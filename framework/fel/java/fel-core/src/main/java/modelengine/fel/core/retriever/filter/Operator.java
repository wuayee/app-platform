/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.core.retriever.filter;

/**
 * 表示过滤表达式的枚举。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
public enum Operator {
    EQ,
    NE,
    LT,
    GT,
    LE,
    GE,
    IN,
    NIN,
    LIKE,
    OR,
    AND,
}