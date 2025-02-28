/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.service;

import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 插件部署服务接口。
 *
 * @author 罗帅
 * @since 2024-8-13
 */
public interface PluginDeployService {
    /**
     * 部署插件。
     *
     * @param pluginIds 表示插件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void deployPlugins(List<String> pluginIds);

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