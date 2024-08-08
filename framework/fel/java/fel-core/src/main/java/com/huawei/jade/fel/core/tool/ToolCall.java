/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.core.tool;

import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.pattern.builder.BuilderFactory;

/**
 * 表示工具调用请求的实体。
 *
 * @author 易文渊
 * @since 2024-04-16
 */
public interface ToolCall {
    /**
     * 获取工具调用的唯一标识。
     *
     * @return 表示工具调用唯一编号的 {@link Integer}。
     */
    @Nonnull
    String id();

    /**
     * 获取调用的工具名称。
     *
     * @return 表示工具名称的 {@link String}。
     */
    @Nonnull
    String name();

    /**
     * 获取工具调用参数。
     *
     * @return 表示工具调用参数的 {@link String}。
     */
    @Nonnull
    String arguments();

    interface Builder {
        /**
         * 设置工具调用的唯一编号。
         *
         * @param id 表示工具调用唯一编号的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder id(String id);

        /**
         * 设置调用的工具名称。
         *
         * @param name 表示工具名称的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder name(String name);

        /**
         * 设置工具调用参数。
         *
         * @param arguments 表示工具调用参数的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder arguments(String arguments);

        /**
         * 构建一个新的工具调用实体。
         *
         * @return 表示 {@link ToolCall} 实例。
         */
        ToolCall build();
    }

    /**
     * 创建 {@link Builder} 的实例。
     *
     * @return 表示创建成功的 {@link Builder}。
     */
    static Builder custom() {
        return BuilderFactory.get(ToolCall.class, Builder.class).create(null);
    }
}