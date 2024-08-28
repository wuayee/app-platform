/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import modelengine.fitframework.annotation.Genericable;

import com.huawei.jade.carver.ListResult;
import com.huawei.jade.store.entity.query.PluginToolQuery;
import com.huawei.jade.store.entity.transfer.PluginToolData;

import java.util.List;

/**
 * 插件工具的服务接口类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
public interface PluginToolService {
    /**
     * 注册一个插件工具。
     *
     * @param pluginToolData 表示待插入的插件工具数据的 {@link PluginToolData}。
     * @return 插件工具的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.tool.addPluginTool")
    String addPluginTool(PluginToolData pluginToolData);

    /**
     * 注册插件工具的列表。
     *
     * @param pluginToolDataList 表示待插入的插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     * @return 插件工具的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.tool.addPluginTools")
    List<String> addPluginTools(List<PluginToolData> pluginToolDataList);

    /**
     * 查询插件工具。
     *
     * @param pluginToolQuery 表示动态查询条件的 {@link PluginToolQuery}。
     * @return 插件工具数据的封装类的 {@link ListResult}{@code <}{@link PluginToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.tool.getPluginTools.byQuery")
    ListResult<PluginToolData> getPluginTools(PluginToolQuery pluginToolQuery);

    /**
     * 根据插件唯一标识查询插件工具列表。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.tool.getPluginTools.byPluginId")
    List<PluginToolData> getPluginTools(String pluginId);

    /**
     * 基于插件工具的唯一标识查询某个插件工具。
     *
     * @param toolUniqueName 表示插件工具的唯一标识的 {@link String}。
     * @return 表示插件工具详细信息的 {@link PluginToolData}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.tool.getPluginTool.byUniqueName")
    PluginToolData getPluginTool(String toolUniqueName);

    /**
     * 删除插件工具。
     *
     * @param toolUniqueName 表示待删除插件工具唯一标识的 {@link String}。
     * @return 表示删除插件工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.tool.deletePluginTool")
    String deletePluginTool(String toolUniqueName);
}
