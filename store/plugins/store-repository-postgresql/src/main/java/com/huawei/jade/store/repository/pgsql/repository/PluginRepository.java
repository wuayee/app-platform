/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository;

import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;

import java.util.List;

/**
 * 插件的仓库。
 *
 * @author 鲁为 l00839724
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
     * @return 插件工具总数的 {@code int}。
     */
    int getPluginsCount(PluginQuery pluginQuery);

    /**
     * 基于插件的唯一标识查询某个插件。
     *
     * @param pluginId 表示插件的唯一标识的 {@link String}。
     * @return 表示插件信息的 {@link PluginDo}。
     */
    PluginDo getPluginByPluginId(String pluginId);
}
