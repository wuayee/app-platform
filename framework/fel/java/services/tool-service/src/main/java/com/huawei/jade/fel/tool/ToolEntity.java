/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.Map;

/**
 * 表示工具的实体类。
 *
 * @author 易文渊
 * @since 2024-08-14
 */
public class ToolEntity implements Tool.Info {
    private String namespace;
    private Map<String, Object> schema;
    private Map<String, Object> runnable;
    private Map<String, Object> extensions;

    public ToolEntity() {}

    public ToolEntity(String group, Map<String, Object> schema, Map<String, Object> runnable,
            Map<String, Object> extensions) {
        this.namespace = group;
        this.schema = schema;
        this.runnable = runnable;
        this.extensions = extensions;
    }

    @Nonnull
    @Override
    public String namespace() {
        return this.namespace;
    }

    @Nonnull
    @Override
    public String name() {
        return ObjectUtils.cast(this.schema.get(ToolSchema.NAME));
    }

    @Nonnull
    @Override
    public String description() {
        return ObjectUtils.cast(this.schema.get(ToolSchema.DESCRIPTION));
    }

    @Nonnull
    @Override
    public Map<String, Object> parameters() {
        return ObjectUtils.cast(this.schema.get(ToolSchema.PARAMETERS));
    }

    @Nonnull
    @Override
    public Map<String, Object> extensions() {
        return this.extensions;
    }

    @Override
    public Map<String, Object> schema() {
        return this.schema;
    }

    @Override
    public Map<String, Object> runnable() {
        return this.runnable;
    }
}