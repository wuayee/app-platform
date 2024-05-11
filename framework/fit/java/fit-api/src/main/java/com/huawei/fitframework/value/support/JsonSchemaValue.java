/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.value.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.value.PropertyValue;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 Json Schema 类型的属性值。
 *
 * @author 季聿阶
 * @since 2024-05-11
 */
public class JsonSchemaValue implements PropertyValue {
    private final String name;
    private final Map<String, Object> schema;

    public JsonSchemaValue(String name, Map<String, Object> schema) {
        this.name = notBlank(name, "The name cannot be blank.");
        this.schema = notNull(schema, "The schema map cannot be null.");
    }

    @Override
    public Class<?> getType() {
        String type = cast(this.schema.get("type"));
        if (StringUtils.isBlank(type)) {
            throw new IllegalStateException("No type info.");
        }
        switch (type) {
            case "string":
                return String.class;
            case "integer":
                return Long.class;
            case "boolean":
                return Boolean.class;
            case "number":
                return Double.class;
            case "object":
                return Map.class;
            case "array":
                return List.class;
            default:
                throw new IllegalStateException(StringUtils.format("No supported type info. [type={0}]", type));
        }
    }

    @Override
    public Type getParameterizedType() {
        return this.getType();
    }

    @Override
    public Optional<AnnotatedElement> getElement() {
        return Optional.empty();
    }

    @Override
    public String getName() {
        return this.name;
    }
}
