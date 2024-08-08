/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.retriever.support;

import com.huawei.jade.fel.core.retriever.filter.AbstractExpressionParser;
import com.huawei.jade.fel.core.retriever.filter.Operand;

/**
 * 表示 {@link com.huawei.jade.fel.core.retriever.filter.ExpressionParser} 的测试实现。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
public class TestExpressionParser extends AbstractExpressionParser {
    @Override
    protected String parseKey(String key) {
        return key;
    }

    @Override
    protected void parseExpression(Operand.Expression expression, StringBuilder buf) {
        this.parseOperand(expression.left(), buf);
        buf.append(" ").append(expression.op()).append(" ");
        this.parseOperand(expression.right(), buf);
    }
}