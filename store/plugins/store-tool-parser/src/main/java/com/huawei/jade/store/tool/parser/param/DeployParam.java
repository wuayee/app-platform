/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.param;

import java.util.List;

/**
 * 部署参数实体类
 *
 * @since 2024/8/13
 */
public class DeployParam {
    List<String> pluginIds;

    /**
     * 获取插件 id 列表。
     *
     * @return 表示任务唯一标识的 {@link List<String>}。
     */
    public List<String> getPluginIds() {
        return this.pluginIds;
    }

    /**
     * 设置插件 id 列表。
     *
     * @param pluginIds 表示插件 id 列表的 {@link List<String>}。
     */
    public void setPluginIds(List<String> pluginIds) {
        this.pluginIds = pluginIds;
    }
}