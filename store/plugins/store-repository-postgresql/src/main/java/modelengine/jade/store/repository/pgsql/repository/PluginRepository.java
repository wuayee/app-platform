/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository;

import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.repository.pgsql.entity.PluginDo;
import modelengine.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 插件的仓库。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
public interface PluginRepository {
    /**
     * 添加插件。
     *
     * @param pluginData 表示待添加的插件的 {@link PluginData}。
     * @return 表示插件的唯一标识的 {@link String}。
     */
    String addPlugin(PluginData pluginData);

    /**
     * 删除插件。
     *
     * @param pluginId 表示待删除的插件的唯一标识的 {@link String}。
     */
    void deletePlugin(String pluginId);

    /**
     * 根据动态查询条件分页查询插件。
     *
     * @param pluginQuery 表示查询参数的实体类的 {@link PluginQuery}。
     * @return 所有任务的实体类的实例的 {@link List}{@code <}{@link PluginDo}{@code >}。
     */
    List<PluginDo> getPlugins(PluginQuery pluginQuery);

    /**
     * 根据动态查询条件分页查询插件的总数。
     *
     * @param pluginQuery 表示查询参数的实体类的 {@link PluginQuery}。
     * @return 插件总数的 {@code int}。
     */
    int getPluginsCount(PluginQuery pluginQuery);

    /**
     * 基于插件的唯一标识查询某个插件。
     *
     * @param pluginId 表示插件的唯一标识的 {@link String}。
     * @return 表示插件信息的 {@link PluginDo}。
     */
    PluginDo getPluginByPluginId(String pluginId);

    /**
     * 根据部署状态查询插件列表。
     *
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     * @return 插件列表的 {@link List}{@code <}{@link PluginData}{@code >}。
     */
    List<PluginDo> getPlugins(DeployStatus deployStatus);

    /**
     * 根据部署状态查询插件的总数。
     *
     * @param deployStatus 表示部署状态的 {@link DeployStatus}。
     * @return 插件总数的 {@code int}。
     */
    int getPluginsCount(DeployStatus deployStatus);

    /**
     * 更新插件列表的部署状态。
     *
     * @param pluginIdList 表示插件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     */
    void updateDeployStatus(List<String> pluginIdList, DeployStatus deployStatus);
}
