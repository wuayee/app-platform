/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema;

import com.huawei.fitframework.json.schema.support.AbstractJsonSchema;
import com.huawei.fitframework.json.schema.support.ArraySchema;
import com.huawei.fitframework.json.schema.support.BooleanSchema;
import com.huawei.fitframework.json.schema.support.IntegerSchema;
import com.huawei.fitframework.json.schema.support.NumberSchema;
import com.huawei.fitframework.json.schema.support.ObjectSchema;
import com.huawei.fitframework.json.schema.support.StringSchema;
import com.huawei.fitframework.json.schema.util.SchemaTypeUtils;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * 表示 Json 格式的规范。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-31
 */
public interface JsonSchema {
    /**
     * 获取 Json 格式规范的类型。
     *
     * @return 表示 Json 格式规范类型的 {@link Type}。
     */
    Type type();

    /**
     * 获取 Json 格式规范的名字。
     *
     * @return 表示 Json 格式规范名字的 {@link String}。
     */
    String name();

    /**
     * 获取 Json 格式规范的描述。
     *
     * @return 表示 Json 格式规范的描述的 {@link String}。
     */
    String description();

    /**
     * 将 Json 格式规范转换成键值对形式。
     *
     * @return 表示转换后的键值对的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> toMap();

    /**
     * 将 Json 格式规范使用 Json 进行输出。
     *
     * @return 表示输出的 Json 字符串的 {@link String}。
     */
    String toJsonString();

    /**
     * 创建一个 Json 格式规范。
     *
     * @param type 表示类型的 {@link Type}。
     * @param referencedPrefix 表示如果存在引用，其引用前缀的 {@link String}。
     * @param referencedSchemas 表示引用数据结果的 {@link Map}{@code <}{@link Type}{@code , }{@link ObjectSchema}{@code >}。
     * @param serializer 表示 Json 序列化其的 {@link ObjectSerializer}。
     * @return 表示创建的格式规范的 {@link JsonSchema}。
     */
    static JsonSchema create(Type type, String referencedPrefix, Map<Type, ObjectSchema> referencedSchemas,
            ObjectSerializer serializer) {
        if (SchemaTypeUtils.isArrayType(type)) {
            return ArraySchema.create(type, referencedPrefix, referencedSchemas, serializer);
        }
        if (SchemaTypeUtils.isObjectType(type)) {
            return ObjectSchema.create(type, referencedPrefix, referencedSchemas, serializer);
        }
        return createPrimitive(type, serializer);
    }

    /**
     * 创建一个除对象和数组格式以外的 Json 格式规范。
     *
     * @param type 表示类型的 {@link Type}。
     * @param serializer 表示 Json 序列化其的 {@link ObjectSerializer}。
     * @return 表示创建的格式规范的 {@link JsonSchema}。
     */
    static AbstractJsonSchema createPrimitive(Type type, ObjectSerializer serializer) {
        Class<?> clazz = ReflectionUtils.ignorePrimitiveClass(TypeUtils.toClass(type));
        if (clazz == String.class || SchemaTypeUtils.isEnumType(type) || clazz == Character.class) {
            return new StringSchema(type, serializer);
        } else if (clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class
                || clazz == BigInteger.class) {
            return new IntegerSchema(type, serializer);
        } else if (clazz == Double.class || clazz == Float.class || clazz == BigDecimal.class) {
            return new NumberSchema(type, serializer);
        } else if (clazz == Boolean.class) {
            return new BooleanSchema(type, serializer);
        } else {
            throw new IllegalStateException(StringUtils.format(
                    "Unsupported Java type to initial json schema. [type={0}]",
                    type.getTypeName()));
        }
    }
}
