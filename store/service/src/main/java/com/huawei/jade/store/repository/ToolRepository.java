/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository;

import com.huawei.jade.store.Tool;

import java.util.List;
import java.util.Optional;

/**
 * 表示大模型的工具仓库。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
public interface ToolRepository {
    /**
     * 获取工具服务的数量。
     *
     * @return 表示工具服务数量的 {@code int}。
     */
    int getToolCount();

    /**
     * 查询 FIT 工具所有的工具组。
     *
     * @param type 表示工具所属类型的  {@link String}。
     * @param offset 表示指定偏移量的 {@code int}。
     * @param limit 表示最大获取工具数量的 {@code int}。
     * @return 表示返回的工具组列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getToolGroups(String type, int offset, int limit);

    /**
     * 查询工具组下所有的工具。
     *
     * @param type 表示工具所属类型的  {@link String}。
     * @param group 表示工具组名字的  {@link String}。
     * @param offset 表示指定偏移量的 {@code int}。
     * @param limit 表示最大获取工具数量的 {@code int}。
     * @return 表示返回的工具组列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getToolNames(String type, String group, int offset, int limit);

    /**
     * 根据 FIT 工具名字和工具组查询工具。
     *
     * @param group 表示工具组名字的  {@link String}。
     * @param name 表示工具名字的  {@link String}。
     * @return 表示指定名字和组的工具的 {@link Optional}{@code <}{@link Tool}{@code >}。
     */
    Optional<Tool> getTool(String group, String name);
}
