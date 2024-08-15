/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.parsers;

import static com.huawei.fitframework.annotation.Order.LOW;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.value.ValueFetcher;
import com.huawei.jade.carver.telemetry.aop.SpanAttributeParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * {@link SpanAttributeParser} 的实现, 表示复杂属性值的解析器，支持结构体、列表和键值对。
 *
 * @author 马朝阳
 * @since 2024-07-29
 */
@Order(LOW)
@Component
public class ComplexSpanAttributeParser implements SpanAttributeParser {
    /**
     * 模式匹配格式：k:v1 或 k:v1,kk:v1.v2 或 k1:$.v1.v2 或 k1:[0].v1 或 k1:$[0].v1.v2
     */
    private static final Pattern FULL_PARSER_REG = Pattern.compile(
            "^\\w+:(\\$\\.|\\$?\\[\\d+\\]\\.|\\w+\\.)*\\w+(,\\w+:(\\$\\.|\\$?\\[\\d+\\]\\.|\\w+\\.)*\\w+)*$");
    private static final Logger LOGGER = Logger.get(ComplexSpanAttributeParser.class);

    private final ValueFetcher fetcher;

    /**
     * 通过值获取工具来初始化 {@link ComplexSpanAttributeParser} 的新实例。
     *
     * @param fetcher 表示值的获取工具的 {@link ValueFetcher}。
     */
    public ComplexSpanAttributeParser(ValueFetcher fetcher) {
        this.fetcher = fetcher;
    }

    /**
     * 解析器是否匹配。
     *
     * @param expression 表示注解表达式的 {@link String}。
     * @return 表示匹配结果的 {@code boolean}。
     */
    @Override
    public boolean match(String expression) {
        if (expression == null) {
            return false;
        }
        return FULL_PARSER_REG.matcher(expression).matches();
    }

    /**
     * 解析属性的键值对。
     *
     * @param expression 表示注解表达式的 {@link String}。
     * @param paramValue 表示被注解的参数值的 {@link Object}。
     * @return 表示属性的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link String}{@code >}。
     */
    @Override
    public Map<String, String> parse(String expression, Object paramValue) {
        if (expression == null) {
            return Collections.emptyMap();
        }
        Map<String, String> expMap = new HashMap<>();
        for (String attr : expression.split(",")) {
            if (attr.isEmpty()) {
                LOGGER.warn("Split attribute fail, expression is {}.", expression);
                continue;
            }
            String[] kv = attr.split(":");
            if (kv.length != 2) {
                LOGGER.warn("Split key-value fail, attribute is {}.", attr);
                continue;
            }
            expMap.put(kv[0].trim(), kv[1].trim());
        }

        return expMap.entrySet().stream().peek(pair -> {
            Object parseRes = this.fetcher.fetch(paramValue, pair.getValue());
            pair.setValue(parseRes == null ? "" : parseRes.toString());
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
