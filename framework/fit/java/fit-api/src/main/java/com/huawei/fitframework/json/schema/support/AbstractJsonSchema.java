/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.json.schema.JsonSchema;
import com.huawei.fitframework.json.schema.util.SchemaTypeUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Type;

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

    /**
     * 通过指定类型和 Json 序列化器来初始化 {@link AbstractJsonSchema} 的新实例。
     *
     * @param type 表示指定类型的 {@link Type}。
     */
    protected AbstractJsonSchema(Type type) {
        this(type, SchemaTypeUtils.getTypeName(type), StringUtils.EMPTY);
    }

    /**
     * 通过指定类型、规范名字、规范描述和 Json 序列化器来初始化 {@link AbstractJsonSchema} 的新实例。
     *
     * @param type 表示指定类型的 {@link Type}。
     * @param name 表示规范名字的 {@link String}。
     * @param description 表示规范描述的 {@link String}。
     */
    protected AbstractJsonSchema(Type type, String name, String description) {
        this.type = notNull(type, "The type cannot be null.");
        this.name = notBlank(name, "The schema name cannot be blank.");
        this.description = description;
    }

    /**
     * 通过指定格式规范和 Json 序列化器来初始化 {@link AbstractJsonSchema} 的新实例。
     *
     * @param schema 表示指定格式规范的 {@link JsonSchema}。
     */
    protected AbstractJsonSchema(JsonSchema schema) {
        notNull(schema, "The json schema cannot be null.");
        this.type = schema.type();
        this.name = schema.name();
        this.description = schema.description();
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
}
