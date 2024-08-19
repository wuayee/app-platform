/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.service;

import com.huawei.fit.http.entity.NamedEntity;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 插件部署服务接口。
 *
 * @author 罗帅
 * @since 2024-8-13
 */
public interface PluginDeployService {
    /**
     * 上传插件。
     *
     * @param namedEntities 表示插件包的 {@link List}{@code <}{@link NamedEntity}{@code >}。
     * @param toolNames 表示工具名称的 {@link String}。
     */
    void uploadPlugins(List<NamedEntity> namedEntities, String toolNames);

    /**
     * 部署插件。
     *
     * @param pluginIds 表示插件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void deployPlugins(List<String> pluginIds);

    /**
     * 删除插件。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     * @return 表示删除数量的 {@code int}。
     */
    int deletePlugin(String pluginId);

    /**
     * 根据部署状态查询插件数量。
     *
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     * @return 表示插件数量的 {@code int}。
     */
    int queryCountByDeployStatus(DeployStatus deployStatus);

    /**
     * 根据部署状态查询插件信息。
     *
     * @param deployStatus 表示插件部署状态的 {@link DeployStatus}。
     * @return 表示插件信息列表的 {@link List}{@code <}{@link PluginData}{@code >}。
     */
    List<PluginData> queryPluginsByDeployStatus(DeployStatus deployStatus);
}