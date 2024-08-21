/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.openapi3.swagger.entity.support;

import com.huawei.fit.http.openapi3.swagger.entity.Schema;
import modelengine.fitframework.util.MapBuilder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link Schema} 的引用实现。
 *
 * @author 季聿阶
 * @since 2023-08-25
 */
public class ReferenceSchema extends AbstractSchema {
    private final String reference;

    public ReferenceSchema(String name, Type type, String description, List<String> examples) {
        super(name, type, description, examples);
        this.reference = "#/components/schemas/" + name;
    }

    @Override
    public Map<String, Object> toJson() {
        MapBuilder<String, Object> builder = MapBuilder.<String, Object>get().put("$ref", this.reference);
        return builder.build();
    }
}
