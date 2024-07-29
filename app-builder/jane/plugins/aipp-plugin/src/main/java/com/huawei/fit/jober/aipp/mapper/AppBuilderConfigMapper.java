/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderConfigPo;

/**
 * AppBuilder配置映射器
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderConfigMapper {
    /**
     * 通过id查询AppBuilder配置
     *
     * @param id 配置id
     * @return AppBuilder配置信息
     */
    AppBuilderConfigPo selectWithId(String id);

    /**
     * 插入一个配置信息
     *
     * @param insert 要插入配置信息
     */
    void insertOne(AppBuilderConfigPo insert);

    /**
     * 更新一个配置信息
     *
     * @param update 要更新的配置信息
     */
    void updateOne(AppBuilderConfigPo update);

    /**
     * 通过AppId查询配置信息
     *
     * @param appId appId
     * @return AppBuilder配置信息
     */
    AppBuilderConfigPo selectWithAppId(String appId);

    /**
     * 删除配置信息
     *
     * @param id 被删除的配置id
     */
    void delete(String id);
}
