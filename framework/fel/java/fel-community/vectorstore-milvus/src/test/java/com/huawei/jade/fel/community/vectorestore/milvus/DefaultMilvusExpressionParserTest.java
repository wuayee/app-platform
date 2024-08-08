/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.community.vectorestore.milvus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.huawei.jade.fel.core.retriever.filter.ExpressionParser;
import com.huawei.jade.fel.core.retriever.filter.Filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 表示 {@link DefaultMilvusExpressionParser} 的测试集。
 *
 * @author 易文渊
 * @since 2024-08-10
 */
@DisplayName("测试 DefaultMilvusExpressionParser")
public class DefaultMilvusExpressionParserTest {
    private final ExpressionParser parser = new DefaultMilvusExpressionParser();

    @Test
    @DisplayName("测试 eq 表达式")
    void testEqExpression() {
        Filter filter = Filter.eq("country", "china");
        assertThat(parser.parse(filter.expression())).isEqualTo("metadata[\"country\"] == \"china\"");
    }

    @Test
    @DisplayName("测试 in 表达式")
    void testInExpression() {
        Filter filter = Filter.in("color", Arrays.asList("blue", "black", "white"));
        assertThat(parser.parse(filter.expression())).isEqualTo(
                "metadata[\"color\"] in [\"blue\",\"black\"," + "\"white\"]");
    }

    @Test
    @DisplayName("测试 and 表达式")
    void testAndExpression() {
        Filter filter = Filter.eq("country", "china").and(Filter.ne("profession", "doctor"));
        assertThat(parser.parse(filter.expression())).isEqualTo(
                "metadata[\"country\"] == \"china\" && metadata[\"profession\"] != \"doctor\"");
    }

    @Test
    @DisplayName("测试 like 表达式")
    void testLikeExpression() {
        Filter filter = Filter.like("story", "dog");
        assertThat(parser.parse(filter.expression())).isEqualTo("metadata[\"story\"] like \"%dog%\"");
    }
}