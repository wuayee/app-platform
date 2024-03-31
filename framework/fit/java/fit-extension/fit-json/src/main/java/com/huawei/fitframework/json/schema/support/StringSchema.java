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
 * 表示 {@link com.huawei.fitframework.json.schema.JsonSchema} 的字符串实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-31
 */
public class StringSchema extends AbstractJsonSchema {
    public StringSchema(Type type, ObjectSerializer serializer) {
        super(type, serializer);
    }

    @Override
    public Map<String, Object> toMap() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("type", "string");
        if (StringUtils.isNotBlank(this.description())) {
            builder.put("description", this.description());
        }
        return builder.build();
    }
}
