/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.service;

import modelengine.jade.store.entity.transfer.PluginData;

/**
 * 表示插件部署的服务。
 *
 * @author 杭潇
 * @since 2025-01-10
 */
public interface DeployService {
    /**
     * 部署插件。
     *
     * @param pluginData 表示插件的数据内容的 {@link PluginData}。
     * @param pluginFullName 表示插件的全名的 {@link String}。
     * @param pluginId 表示插件的唯一性值的 {@link String}。
     */
    void deploy(PluginData pluginData, String pluginFullName, String pluginId);
}
