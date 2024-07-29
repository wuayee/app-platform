/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import java.util.Map;

/**
 * {@link io.opentelemetry.instrumentation.annotations.SpanAttribute} 表达式的解析器。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
public interface SpanAttributeParser {
    /**
     * 解析器是否匹配。
     *
     * @param expression 表示注解表达式的 {@link String}。
     * @return 表示匹配结果的 {@code boolean}。
     */
    boolean match(String expression);

    /**
     * 解析属性的键值对。
     *
     * @param expression 表示注解表达式的 {@link String}。
     * @param paramValue 表示被注解的参数值的 {@link Object}。
     * @return 表示属性的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    Map<String, String> parse(String expression, Object paramValue);
}
