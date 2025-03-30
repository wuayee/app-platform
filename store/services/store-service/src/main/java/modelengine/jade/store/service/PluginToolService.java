/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.carver.ListResult;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;

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
    @Deprecated
    @Genericable(id = "modelengine.jade.store.plugin.tool.addPluginTool")
    String addPluginTool(PluginToolData pluginToolData);

    /**
     * 注册插件工具的列表。
     *
     * @param pluginToolDataList 表示待插入的插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     * @return 插件工具的唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.addPluginTools")
    List<String> addPluginTools(List<PluginToolData> pluginToolDataList);

    /**
     * 查询插件工具。
     *
     * @param pluginToolQuery 表示动态查询条件的 {@link PluginToolQuery}。
     * @return 插件工具数据的封装类的 {@link ListResult}{@code <}{@link PluginToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.getPluginTools.byQuery")
    ListResult<PluginToolData> getPluginTools(PluginToolQuery pluginToolQuery);

    /**
     * 根据插件唯一标识查询插件工具列表。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.getPluginTools.byPluginId")
    List<PluginToolData> getPluginTools(String pluginId);

    /**
     * 根据插件唯一标识列表查询插件工具列表。
     *
     * @param uniqueNames 表示插件工具唯一标识集合的 {@link List}{@code <}{@link String}{@code >}。
     * @return 插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.getPluginTools.byUniqueNames")
    List<PluginToolData> getPluginTools(List<String> uniqueNames);

    /**
     * 根据插件工具唯一标识列表检查插件工具是否存在。
     *
     * @param uniqueNames 表示插件工具唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 插件工具是否存在的 {@link List}{@code <}{@link Boolean}{@code >}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.hasPluginTools")
    List<Boolean> hasPluginTools(List<String> uniqueNames);

    /**
     * 基于插件工具的唯一标识查询某个插件工具。
     *
     * @param toolUniqueName 表示插件工具的唯一标识的 {@link String}。
     * @return 表示插件工具详细信息的 {@link PluginToolData}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.getPluginTool.byUniqueName")
    PluginToolData getPluginTool(String toolUniqueName);

    /**
     * 删除插件工具。
     *
     * @param toolUniqueName 表示待删除插件工具唯一标识的 {@link String}。
     * @return 表示删除插件工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.store.plugin.tool.deletePluginTool")
    String deletePluginTool(String toolUniqueName);
}
