/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;

/**
 * 插件的服务接口类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public interface PluginService {
    /**
     * 注册插件。
     *
     * @param pluginData 表示带插入的插件数据的 {@link PluginData}。
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
     * 基于插件的唯一标识查询某个插件。
     *
     * @param toolUniqueName 表示插件的唯一标识的 {@link String}。
     * @return 表示插件详细信息的 {@link ToolData}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.getPlugin.byUniqueName")
    PluginData getPlugin(String toolUniqueName);

    /**
     * 删除插件。
     *
     * @param toolUniqueName 表示待删除插件唯一标识的 {@link String}。
     * @return 表示删除插件的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.plugin.deletePlugin")
    String deletePlugin(String toolUniqueName);
}
