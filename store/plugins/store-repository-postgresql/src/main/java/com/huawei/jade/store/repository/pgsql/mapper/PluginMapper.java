/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.mapper;

import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.repository.pgsql.entity.PluginDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Plugin 接口。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public interface PluginMapper {
    /**
     * 注册插件。
     *
     * @param pluginDo 表示待增加的插件信息的 {@link PluginDo}。
     */
    void addPlugin(PluginDo pluginDo);

    /**
     * 基于工具的唯一标识查询某个插件。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示插件信息的 {@link PluginDo}。
     */
    PluginDo getPluginByUniqueName(String uniqueName);

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
     * @return 所有任务的实体类的实例的 {@code int}。
     */
    int getPluginsCount(PluginQuery pluginQuery);

    /**
     * 根据动态查询条件分页查询插件的总数。
     *
     * @param pluginQuery 表示收藏者的 {@link String}。
     * @return 所有插件的唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<PluginDo> getMyCollection(PluginQuery pluginQuery);

    /**
     * 删除插件。
     *
     * @param uniqueName 表示工具名的 {@link String}。
     */
    void deletePlugin(String uniqueName);
}
