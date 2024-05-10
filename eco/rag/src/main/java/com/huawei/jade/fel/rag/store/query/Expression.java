/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.rag.store.query;

import lombok.Getter;

/**
 * 表达式封装。
 *
 * @since 2024-05-07
 */
@Getter
public class Expression implements Element {
    private ExpressionType type;
    private Element left;
    private Element right;

    /**
     * 根据表达式类型、左右表达式构建 {@link Expression} 实例。
     *
     * @param type 表示表达式类型的 {@link ExpressionType}。
     * @param left 表示表达式左侧元素的 {@link Element}。
     * @param right 表示表达式右侧元素的 {@link Element}。
     */
    public Expression(ExpressionType type, Element left, Element right) {
        this.type = type;
        this.left = left;
        this.right = right;
    }
}
