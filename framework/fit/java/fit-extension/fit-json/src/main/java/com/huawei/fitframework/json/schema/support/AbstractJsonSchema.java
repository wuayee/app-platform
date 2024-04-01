/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.json.schema.JsonSchema;
import com.huawei.fitframework.json.schema.util.SchemaTypeUtils;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link JsonSchema} 的抽象父类。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-31
 */
public abstract class AbstractJsonSchema implements JsonSchema {
    private final Type type;
    private final String name;
    private final String description;

    private final ObjectSerializer serializer;

    /**
     * 通过指定类型和 Json 序列化器来初始化 {@link AbstractJsonSchema} 的新实例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     */
    protected AbstractJsonSchema(Type type, ObjectSerializer serializer) {
        this.type = notNull(type, "The type cannot be null.");
        this.name = SchemaTypeUtils.getTypeName(this.type);
        this.description = StringUtils.EMPTY;
        this.serializer = notNull(serializer, "The json serializer can not be null.");
    }

    /**
     * 通过指定格式规范和 Json 序列化器来初始化 {@link AbstractJsonSchema} 的新实例。
     *
     * @param schema 表示指定格式规范的 {@link JsonSchema}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     */
    protected AbstractJsonSchema(JsonSchema schema, ObjectSerializer serializer) {
        notNull(schema, "The json schema cannot be null.");
        this.type = schema.type();
        this.name = schema.name();
        this.description = schema.description();
        this.serializer = notNull(serializer, "The json serializer can not be null.");
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String toJsonString() {
        return new String(this.serializer.serialize(this.toMap(), StandardCharsets.UTF_8));
    }

    /**
     * 获取 Json 的序列化器。
     *
     * @return 表示 Json 序列化器的 {@link ObjectSerializer}。
     */
    protected ObjectSerializer serializer() {
        return this.serializer;
    }
}
