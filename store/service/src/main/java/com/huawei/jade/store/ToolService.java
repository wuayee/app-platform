/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store;

import java.util.List;
import java.util.Optional;

/**
 * 表示大模型的工具服务。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface ToolService {
    /**
     * 获取工具服务的数量。
     *
     * @return 表示工具服务数量的 {@code int}。
     */
    int getToolCount();

    /**
     * 获取工具服务列表。
     *
     * @param offset 表示指定偏移量的 {@code int}。
     * @param limit 表示最大获取工具数量的 {@code int}。
     * @return 表示获取的工具列表的 {@link List}{@code <}{@link Tool}{@code >}。
     */
    List<Tool> getTools(int offset, int limit);

    /**
     * 获取指定名字的工具。
     *
     * @param name 表示指定工具名字的 {@link String}。
     * @return 表示指定名字的工具的 {@link Optional}{@code <}{@link Tool}{@code >}。
     */
    Optional<Tool> getTool(String name);
}
