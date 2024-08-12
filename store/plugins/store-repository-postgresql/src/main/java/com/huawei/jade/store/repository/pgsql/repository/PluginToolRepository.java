/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository;

import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.entity.transfer.PluginToolData;
import com.huawei.jade.store.repository.pgsql.entity.PluginToolDo;

import java.util.List;

/**
 * 插件工具的仓库。
 *
 * @author 鲁为
 * @since 2024-07-18
 */
public interface PluginToolRepository {
    /**
     * 添加插件工具。
     *
     * @param pluginToolData 表示待添加的插件工具的 {@link PluginToolData}。
     */
    void addPluginTool(PluginToolData pluginToolData);

    /**
     * 添加插件工具。
     *
     * @param pluginToolDataList 表示待添加的插件工具的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    void addPluginTools(List<PluginToolData> pluginToolDataList);

    /**
     * 删除插件工具。
     *
     * @param toolUniqueName 表示待删除的插件工具的唯一标识的 {@link String}。
     */
    void deletePluginTool(String toolUniqueName);

    /**
     * 根据动态查询条件分页查询插件工具。
     *
     * @param pluginToolQuery 表示查询参数的实体类的 {@link PluginToolQuery}。
     * @return 插件工具列表的 {@link List}{@code <}{@link PluginToolDo}{@code >}。
     */
    List<PluginToolDo> getPluginTools(PluginToolQuery pluginToolQuery);

    /**
     * 根据插件唯一标识查询插件工具列表。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 插件工具列表的 {@link List}{@code <}{@link PluginToolDo}{@code >}。
     */
    List<PluginToolDo> getPluginTools(String pluginId);

    /**
     * 根据动态查询条件分页查询插件工具的总数。
     *
     * @param pluginToolQuery 表示查询参数的实体类的 {@link PluginToolQuery}。
     * @return 插件工具总数的 {@code int}。
     */
    int getPluginToolsCount(PluginToolQuery pluginToolQuery);

    /**
     * 基于工具的唯一标识查询某个插件工具。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示插件信息的 {@link PluginToolDo}。
     */
    PluginToolDo getPluginToolByUniqueName(String toolUniqueName);
}
