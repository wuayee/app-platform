/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;

/**
 * 应用的服务接口类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-15
 */
public interface AppService {
    /**
     * 添加应用。
     *
     * @param appData 表示待增加的工具信息的 {@link AppData}。
     * @return 表示添加后应用的唯一标识的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.app.addApp")
    String addApp(AppData appData);

    /**
     * 查询插件。
     *
     * @param pluginQuery 表示动态查询条件的 {@link AppQuery}。
     * @return 插件数据的封装类的 {@link ListResult}{@code <}{@link AppData}{@code >}。
     */
    @Genericable(id = "com.huawei.jade.store.app.getApps")
    ListResult<AppData> getApps(AppQuery pluginQuery);

    /**
     * 基于应用的唯一标识查询某个应用。
     *
     * @param toolUniqueName 表示应用的唯一标识的 {@link String}。
     * @return 表示应用详细信息的 {@link ToolData}。
     */
    @Genericable(id = "com.huawei.jade.store.app.getApp.byUniqueName")
    AppData getApp(String toolUniqueName);

    /**
     * 删除插件。
     *
     * @param toolUniqueName 表示待删除插件唯一标识的 {@link String}。
     * @return 表示删除插件的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.app.deleteApp")
    String deleteApp(String toolUniqueName);
}
