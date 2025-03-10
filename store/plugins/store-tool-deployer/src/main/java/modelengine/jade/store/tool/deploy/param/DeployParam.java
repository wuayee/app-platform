/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.param;

import java.util.List;

/**
 * 部署参数实体类。
 *
 * @author 罗帅
 * @since 2024-8-13
 */
public class DeployParam {
    List<String> pluginIds;

    /**
     * 获取插件唯一标识列表。
     *
     * @return 表示插件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> getPluginIds() {
        return this.pluginIds;
    }

    /**
     * 设置插件唯一标识列表。
     *
     * @param pluginIds 表示插件唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void setPluginIds(List<String> pluginIds) {
        this.pluginIds = pluginIds;
    }
}