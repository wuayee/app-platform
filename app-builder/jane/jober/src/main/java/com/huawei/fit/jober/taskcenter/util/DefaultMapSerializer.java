/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 为{@link MapSerializer}提供默认实现
 */
@Component
class DefaultMapSerializer implements MapSerializer {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Type TYPE = TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});

    private final ObjectSerializer serializer;

    public DefaultMapSerializer(@Fit(alias = "json") ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String serialize(Map<String, Object> value) {
        byte[] bytes = this.serializer.serialize(value, CHARSET);
        return new String(bytes, CHARSET);
    }

    @Override
    public Map<String, Object> deserialize(String value) {
        byte[] bytes = value.getBytes(CHARSET);
        return this.serializer.deserialize(bytes, CHARSET, TYPE);
    }
}
