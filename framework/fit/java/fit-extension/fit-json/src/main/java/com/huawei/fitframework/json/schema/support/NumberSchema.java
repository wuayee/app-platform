/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.json.schema.support;

import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 表示 {@link com.huawei.fitframework.json.schema.JsonSchema} 的浮点数值实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-31
 */
public class NumberSchema extends AbstractJsonSchema {
    public NumberSchema(Type type, ObjectSerializer serializer) {
        super(type, serializer);
    }

    @Override
    public Map<String, Object> toMap() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "number");
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }
}
