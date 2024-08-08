/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.vectorestore.milvus;

import static com.huawei.jade.fel.community.vectorestore.milvus.MilvusVectorStore.METADATA_FIELD_NAME;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.fel.core.retriever.filter.AbstractExpressionParser;
import com.huawei.jade.fel.core.retriever.filter.Operand;
import com.huawei.jade.fel.core.retriever.filter.Operator;

import java.util.Map;

/**
 * 表示 {@link com.huawei.jade.fel.core.retriever.filter.ExpressionParser} 的 milvus 实现。
 *
 * @author 易文渊
 * @see <a href="https://milvus.io/docs/get-and-scalar-query.md">milvus-scalar-query</a>
 * @since 2024-08-10
 */
public class DefaultMilvusExpressionParser extends AbstractExpressionParser {
    private static final Map<Operator, String> OPERATOR_SYMBOLS = MapBuilder.<Operator, String>get()
            .put(Operator.EQ, "==")
            .put(Operator.NE, "!=")
            .put(Operator.LT, "<")
            .put(Operator.GT, ">")
            .put(Operator.LE, "<=")
            .put(Operator.GE, ">=")
            .put(Operator.IN, "in")
            .put(Operator.NIN, "not in")
            .put(Operator.LIKE, "like")
            .put(Operator.AND, "&&")
            .put(Operator.OR, "||")
            .build();

    @Override
    protected String parseKey(String key) {
        return StringUtils.format("{0}[\"{1}\"]", METADATA_FIELD_NAME, key);
    }

    @Override
    protected void parseExpression(Operand.Expression expression, StringBuilder buf) {
        Operator op = expression.op();
        Operand left = expression.left();
        Operand right = expression.right();

        this.parseOperand(left, buf);
        String symbol = OPERATOR_SYMBOLS.get(op);
        Validation.notNull(symbol,
                () -> new UnsupportedOperationException(StringUtils.format("The operator {0} is nonsupport.", op)));
        buf.append(" ").append(symbol).append(" ");
        if (op == Operator.LIKE) {
            Operand.Value value =
                    Validation.isInstanceOf(right, Operand.Value.class, "The operator like cannot be match expression");
            Validation.isInstanceOf(value.payload(), String.class, "The operator like must match string.");
            buf.append(StringUtils.format("\"%{0}%\"", value.payload()));
        } else {
            this.parseOperand(right, buf);
        }
    }
}