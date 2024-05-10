/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderConfigProperty;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderConfigPropertyRepository {
    List<AppBuilderConfigProperty> selectWithConfigId(String configId);

    AppBuilderConfigProperty selectWithId(String id);

    void insertOne(AppBuilderConfigProperty appBuilderConfigProperty);

    void insertMore(List<AppBuilderConfigProperty> appBuilderConfigProperties);

    void updateOne(AppBuilderConfigProperty appBuilderConfigProperty);

    int deleteMore(List<String> ids);
}
