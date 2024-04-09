/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fitframework.json.schema.JsonSchema;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link JsonSchema} 的装饰器实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-31
 */
public class DecoratedSchema extends AbstractJsonSchema {
    private final String name;
    private final String description;
    private final String defaultValue;
    private final JsonSchema schema;

    DecoratedSchema(String name, String description, String defaultValue, JsonSchema schema) {
        super(schema);
        this.name = notBlank(name, "The decorated name cannot be blank.");
        this.description = description;
        this.defaultValue = defaultValue;
        this.schema = schema;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        if (StringUtils.isNotBlank(this.description)) {
            return this.description;
        } else {
            return super.description();
        }
    }

    @Override
    public Map<String, Object> toJsonObject() {
        Map<String, Object> map = this.schema.toJsonObject();
        if (StringUtils.isNotBlank(this.description())) {
            map.put("description", this.description());
        }
        if (StringUtils.isNotBlank(this.defaultValue)) {
            map.put("default", this.defaultValue);
        }
        return map;
    }
}
