/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import com.huawei.jade.carver.telemetry.aop.parsers.DefaultSpanAttributeParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 表达式解析器的工厂。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
public class SpanAttributeParserFactory {
    private final List<SpanAttributeParser> parses = new ArrayList<>();

    private SpanAttributeParserFactory() {}

    /**
     * 实例化工厂对象。
     *
     * @return {@link SpanAttributeParserFactory} 对象。
     */
    public static SpanAttributeParserFactory create() {
        return new SpanAttributeParserFactory();
    }

    /**
     * 生产表达式解析器。
     *
     * @return 表示表达式解析器列表的 {@link List}{@code <}{@link SpanAttributeParser}{@code >}。
     */
    public List<SpanAttributeParser> build() {
        this.parses.add(new DefaultSpanAttributeParser());
        return this.parses;
    }
}
