/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.domain.AppBuilderApp;

import java.util.List;

/**
 * app 相关数据库操作对象
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderAppRepository {
    /**
     * 根据 app 唯一标识获取 app 对象。
     *
     * @param id 表示 app 的唯一标识的 {@link String}。
     * @return 表示获取到的 app 对象的 {@link AppBuilderApp}。
     */
    AppBuilderApp selectWithId(String id);

    /**
     * 根据查询条件获取最新的 app 对象列表。
     *
     * @param cond 表示 app 的唯一标识的 {@link String}。
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @param offset 表示偏移量的 {@code offset}。
     * @param limit 表示获取到的个数的 {@code int}。
     * @return 表示获取到的最新的 app 对象列表的 {@link List}{@code <}{@link AppBuilderApp}{@code >}。
     */
    List<AppBuilderApp> selectWithLatestApp(AppQueryCondition cond, String tenantId,
            long offset, int limit);

    /**
     * 根据查询条件获取的 app 对象列表。
     *
     * @param cond 表示 app 的唯一标识的 {@link String}。
     * @return 表示获取到的 app 对象列表的 {@link List}{@code <}{@link AppBuilderApp}{@code >}。
     */
    List<AppBuilderApp> selectWithCondition(AppQueryCondition cond);

    /**
     * 根据查询条件获取最新的 app 对象数量。
     *
     * @param tenantId 表示租户 id 的唯一标识的 {@link String}。
     * @param cond 表示过滤条件的 {@link AppQueryCondition}。
     * @return 表示获取到的最新的 app 对象数量的 {@code long}。
     */
    long countWithLatestApp(String tenantId, AppQueryCondition cond);

    /**
     * 数据插入。
     *
     * @param appBuilderApp 表示需要插入的 app 对象的 {@link AppBuilderApp}。
     */
    void insertOne(AppBuilderApp appBuilderApp);

    /**
     * 数据更新。
     *
     * @param appBuilderApp 表示需要更新的 app 对象的 {@link AppBuilderApp}。
     */
    void updateOne(AppBuilderApp appBuilderApp);

    /**
     * 批量删除数据
     *
     * @param appIds 表示需要删除的 app 对象唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    void delete(List<String> appIds);
}
