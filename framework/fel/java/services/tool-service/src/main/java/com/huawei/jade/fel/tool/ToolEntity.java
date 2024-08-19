/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool;

import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.inspection.Nonnull;

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

    /**
     * 默认构造函数。
     */
    public ToolEntity() {}

    /**
     * 创建 {@link ToolEntity} 的实例。
     *
     * @param namespace 表示工具命名空间的 {@link String}。
     * @param schema 表示工具格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param runnable 表示工具运行规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param extensions 表示工具额外参数规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public ToolEntity(String namespace, Map<String, Object> schema, Map<String, Object> runnable,
            Map<String, Object> extensions) {
        this.namespace = notNull(namespace, "The namespace cannot be null.");
        this.schema = notNull(schema, "The schema cannot be null.");
        this.runnable = notNull(runnable, "The runnable cannot be null.");
        this.extensions = notNull(extensions, "The extensions cannot be null.");
    }

    @Nonnull
    @Override
    public String namespace() {
        return this.namespace;
    }

    @Nonnull
    @Override
    public String name() {
        return cast(this.schema.get(ToolSchema.NAME));
    }

    @Nonnull
    @Override
    public String description() {
        return cast(this.schema.get(ToolSchema.DESCRIPTION));
    }

    @Nonnull
    @Override
    public Map<String, Object> parameters() {
        return cast(this.schema.get(ToolSchema.PARAMETERS));
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