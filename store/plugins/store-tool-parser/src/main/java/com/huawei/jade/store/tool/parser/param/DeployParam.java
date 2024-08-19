/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.param;

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