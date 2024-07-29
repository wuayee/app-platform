/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.telemetry.aop.parsers;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.telemetry.aop.SpanAttributeParser;

import java.util.Collections;
import java.util.Map;

/**
 * {@link SpanAttributeParser} 默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
public class DefaultSpanAttributeParser implements SpanAttributeParser {
    @Override
    public boolean match(String expression) {
        return true;
    }

    @Override
    public Map<String, String> parse(String expression, Object paramValue) {
        return Collections.singletonMap(expression, paramValue == null ? StringUtils.EMPTY : paramValue.toString());
    }
}
