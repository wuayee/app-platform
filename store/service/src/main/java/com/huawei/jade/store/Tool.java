/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import java.util.Map;

/**
 * 表示大模型的工具。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface Tool {
    /**
     * 获取工具的类型。
     *
     * @return 表示工具类型的 {@link String}。
     */
    String type();

    /**
     * 获取工具的名字。
     *
     * @return 表示工具名字的 {@link String}。
     */
    String name();

    /**
     * 获取工具的描述。
     *
     * @return 表示工具描述的 {@link String}。
     */
    String description();

    /**
     * 获取工具的格式规范描述。
     *
     * @return 表示工具的格式规范描述的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    Map<String, Object> schema();
}
