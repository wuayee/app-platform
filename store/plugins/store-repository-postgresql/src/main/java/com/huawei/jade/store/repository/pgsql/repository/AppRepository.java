/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.repository;

import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.repository.pgsql.entity.AppDo;

import java.util.List;

/**
 * 应用的仓库。
 *
 * @author 鲁为
 * @since 2024-07-12
 */
public interface AppRepository {
    /**
     * 添加应用。
     *
     * @param appData 表示待添加的应用的 {@link AppData}。
     */
    void addApp(AppData appData);

    /**
     * 查询应用列表。
     *
     * @param appQuery 表示应用的查询条件的 {@link AppQuery}。
     * @return 应用信息的列表的 {@link List}{@code <}{@link AppDo}{@code >}。
     */
    List<AppDo> getApps(AppQuery appQuery);

    /**
     * 查询应用总数。
     *
     * @param appQuery 表示应用的查询条件的 {@link AppQuery}。
     * @return 应用总数的 {@code int}。
     */
    int getAppsCount(AppQuery appQuery);

    /**
     * 查询应用。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 应用信息的 {@link AppDo}。
     */
    AppDo getApp(String toolUniqueName);

    /**
     * 删除应用。
     *
     * @param toolUniqueName 表示应用的唯一标识的 {@link String}。
     */
    void deleteApp(String toolUniqueName);
}
