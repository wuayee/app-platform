/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop;

import com.huawei.jade.carver.telemetry.aop.parsers.DefaultSpanAttributeParser;

import java.util.Arrays;
import java.util.List;

/**
 * 表达式解析器的仓库。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
public class SpanAttributeParserRepository {
    private static final List<SpanAttributeParser> PARSERS = Arrays.asList(
            new DefaultSpanAttributeParser()
    );

    /**
     * 获取表达式解析器。
     *
     * @return 表示表达式解析器列表的 {@link List}{@code <}{@link SpanAttributeParser}{@code >}。
     */
    public static List<SpanAttributeParser> get() {
        return PARSERS;
    }
}
