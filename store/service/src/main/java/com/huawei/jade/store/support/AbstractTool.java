/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.Tool;

import java.util.Map;

/**
 * 表示工具的抽象实现。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public abstract class AbstractTool implements Tool {
    private final String type;
    private final String name;
    private final String description;

    /**
     * 通过工具类型、名字和描述来初始化 {@link AbstractTool} 的新实例。
     *
     * @param type 表示工具类型的 {@link String}。
     * @param name 表示工具名字的 {@link String}。
     * @param description 表示工具描述的 {@link String}。
     */
    protected AbstractTool(String type, String name, String description) {
        this.type = notBlank(type, "The tool type cannot be blank.");
        this.name = notBlank(name, "The tool name cannot be blank.");
        this.description = nullIf(description, StringUtils.EMPTY);
    }

    /**
     * 通过工具类型和工具格式规范来初始化 {@link AbstractTool} 的新实例。
     *
     * @param type 表示工具类型的 {@link String}。
     * @param toolSchema 表示工具格式规范的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    protected AbstractTool(String type, Map<String, Object> toolSchema) {
        this.type = notBlank(type, "The tool type cannot be blank.");
        notNull(toolSchema, "The json schema cannot be null.");
        this.name = notBlank(cast(toolSchema.get("name")), "The tool name cannot be blank.");
        this.description = nullIf(cast(toolSchema.get("description")), StringUtils.EMPTY);
    }

    @Override
    public String type() {
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
