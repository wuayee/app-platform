/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 表示 {@link com.huawei.fitframework.json.schema.JsonSchema} 的布尔值实现。
 *
 * @author 季聿阶
 * @since 2024-03-31
 */
public class BooleanSchema extends AbstractJsonSchema {
    public BooleanSchema(Type type) {
        super(type);
    }

    @Override
    public Map<String, Object> toJsonObject() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "boolean");
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }
}
