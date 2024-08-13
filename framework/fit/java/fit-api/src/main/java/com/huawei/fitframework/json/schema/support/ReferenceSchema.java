/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.json.schema.JsonSchema;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link JsonSchema} 的引用实现。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public class ReferenceSchema extends AbstractJsonSchema {
    private final String reference;
    private final JsonSchema schema;

    ReferenceSchema(String reference, JsonSchema schema) {
        super(schema);
        this.reference = nullIf(reference, StringUtils.EMPTY);
        this.schema = schema;
    }

    @Override
    public Map<String, Object> toJsonObject() {
        return MapBuilder.<String, Object>get()
                .put("type", "object")
                .put("#ref", this.reference + this.schema.name())
                .build();
    }
}
