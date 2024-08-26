/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import modelengine.fitframework.annotation.Genericable;

import com.huawei.jade.carver.ListResult;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 插件的服务接口类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
public interface PluginService {
    /**
     * 注册插件。
     *
     * @param pluginData 表示待插入的插件数据的 {@link PluginData}。
     * @return 插件的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.addPlugin")
    String addPlugin(PluginData pluginData);

    /**
     * 查询插件。
     *
     * @param pluginQuery 表示动态查询条件的 {@link PluginQuery}。
     * @return 插件数据的封装类的 {@link ListResult}{@code <}{@link PluginData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.getPlugins")
    ListResult<PluginData> getPlugins(PluginQuery pluginQuery);

    /**
     * 根据部署状态查询插件列表。
     *
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     * @return 插件列表的 {@link List}{@code <}{@link PluginData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.getPlugins.byDeployStatus")
    List<PluginData> getPlugins(DeployStatus deployStatus);

    /**
     * 根据部署状态查询插件数量。
     *
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     * @return 插件数量的 {@code int}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.getPluginsCount.byDeployStatus")
    int getPluginsCount(DeployStatus deployStatus);

    /**
     * 更新插件列表的部署状态。
     *
     * @param pluginIdList 表示插件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.updateDeployStatus")
    void updateDeployStatus(List<String> pluginIdList, DeployStatus deployStatus);

    /**
     * 基于插件的唯一标识查询某个插件。
     *
     * @param pluginId 表示插件的唯一标识的 {@link String}。
     * @return 表示插件详细信息的 {@link PluginData}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.getPlugin.byUniqueName")
    PluginData getPlugin(String pluginId);

    /**
     * 删除插件。
     *
     * @param pluginId 表示待删除插件唯一标识的 {@link String}。
     * @return 表示删除插件的唯一标识或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.deletePlugin")
    String deletePlugin(String pluginId);
}
