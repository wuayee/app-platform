/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfig;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderConfigRepository {
    AppBuilderConfig selectWithId(String id);

    void insertOne(AppBuilderConfig appBuilderConfig);

    void updateOne(AppBuilderConfig appBuilderConfig);

    AppBuilderConfig selectWithAppId(String appId);
}
