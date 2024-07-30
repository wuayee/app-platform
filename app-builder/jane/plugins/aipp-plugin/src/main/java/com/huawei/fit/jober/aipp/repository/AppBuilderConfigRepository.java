/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;

/**
 * AppBuilder配置持久化层
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderConfigRepository {
    /**
     * 通过配置id查询配置信息
     *
     * @param id 要查询的配置的id
     * @return 查询到的配置信息
     */
    AppBuilderConfig selectWithId(String id);

    /**
     * 插入一条配置信息
     *
     * @param appBuilderConfig 要插入的配置信息
     */
    void insertOne(AppBuilderConfig appBuilderConfig);

    /**
     * 更新一条配置信息
     *
     * @param appBuilderConfig 要更新的配置信息
     */
    void updateOne(AppBuilderConfig appBuilderConfig);

    /**
     * 通过appId查询配置信息
     *
     * @param appId 要查询的配置信息的appId
     * @return 查询到的配置信息
     */
    AppBuilderConfig selectWithAppId(String appId);

    /**
     * 通过配置id删除配置信息
     *
     * @param id 被删除的配置信息的id
     */
    void delete(String id);
}
