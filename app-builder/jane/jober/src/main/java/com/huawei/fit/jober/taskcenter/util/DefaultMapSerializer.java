/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.util;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
class DefaultMapSerializer implements MapSerializer {
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final Type TYPE = TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});

    public DefaultMapSerializer(@Fit(alias = "json") ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    private final ObjectSerializer serializer;

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
