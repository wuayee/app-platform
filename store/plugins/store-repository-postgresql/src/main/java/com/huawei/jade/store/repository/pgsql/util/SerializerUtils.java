/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.util;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 表示序列化的工具类。
 *
 * @author 季聿阶
 * @since 2024-06-07
 */
public class SerializerUtils {
    private static final ParameterizedType TYPE =
            TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});

    /**
     * 将 Json 格式化数据反序列化为一个键值对。
     *
     * @param schema 表示待序列化的字符串 {@link String}。
     * @param serializer 表示 Json 序列化对象的 {@link ObjectSerializer}。
     * @return 序列化的结果的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    public static Map<String, Object> json2obj(String schema, ObjectSerializer serializer) {
        if (schema != null) {
            return serializer.deserialize(schema, TYPE);
        }
        return null;
    }
}
