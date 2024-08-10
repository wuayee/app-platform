/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.mapper;

import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.repository.pgsql.entity.PluginToolDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 PluginTool 接口。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public interface PluginToolMapper {
    /**
     * 注册插件工具。
     *
     * @param pluginToolDo 表示待增加的插件信息的 {@link PluginToolDo}。
     */
    void addPluginTool(PluginToolDo pluginToolDo);

    /**
     * 添加插件工具的列表。
     *
     * @param pluginToolDoList 表示待添加的插件工具的 {@link List}{@code <}{@link PluginToolDo}{@code >}。
     */
    void addPluginTools(List<PluginToolDo> pluginToolDoList);

    /**
     * 基于工具的唯一标识查询某个插件工具。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示插件工具信息的 {@link PluginToolDo}。
     */
    PluginToolDo getPluginToolByUniqueName(String toolUniqueName);

    /**
     * 根据动态查询条件分页查询插件。
     *
     * @param pluginToolQuery 表示查询参数的实体类的 {@link PluginToolQuery}。
     * @return 所有任务的实体类的实例的 {@link List}{@code <}{@link PluginToolDo}{@code >}。
     */
    List<PluginToolDo> getPluginTools(PluginToolQuery pluginToolQuery);

    /**
     * 根据插件唯一标识查询插件工具列表。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 插件工具列表的 {@link List}{@code <}{@link PluginToolDo}{@code >}。
     */
    List<PluginToolDo> getPluginToolsByPluginId(String pluginId);

    /**
     * 根据动态查询条件分页查询插件的总数。
     *
     * @param pluginToolQuery 表示查询参数的实体类的 {@link PluginToolQuery}。
     * @return 插件总数的 {@code int}。
     */
    int getPluginToolsCount(PluginToolQuery pluginToolQuery);

    /**
     * 根据工具唯一标识删除插件工具。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     */
    void deletePluginTool(String toolUniqueName);
}
