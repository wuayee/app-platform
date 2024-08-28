/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.task.domain.type;

import modelengine.fitframework.util.ParsingResult;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.support.DefaultParsingResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 为布尔值提供数据转换器。
 *
 * @author 梁济时
 * @since 2024-01-23
 */
public class BooleanConverter extends AbstractScalarDataConverter {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final BooleanConverter INSTANCE = new BooleanConverter();

    private final Set<String> trueTexts;

    private BooleanConverter() {
        this.trueTexts = new HashSet<>(Arrays.asList("true", "yes", "on", "ok"));
    }

    @Override
    protected Object fromExternal0(Object value) {
        if (value instanceof Boolean) {
            return value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        String text = StringUtils.trim(value.toString());
        text = StringUtils.toLowerCase(text);
        return this.trueTexts.contains(text);
    }

    @Override
    protected Object toExternal0(Object value) {
        return value;
    }

    @Override
    protected Object fromPersistence0(Object value) {
        return fromExternal0(value);
    }

    @Override
    protected Object toPersistence0(Object value) {
        return value;
    }

    @Override
    protected ParsingResult<Object> parse0(String text) {
        return new DefaultParsingResult<>(true, this.trueTexts.contains(StringUtils.toLowerCase(text)));
    }

    @Override
    protected String toString0(Object value) {
        return value.toString();
    }
}
