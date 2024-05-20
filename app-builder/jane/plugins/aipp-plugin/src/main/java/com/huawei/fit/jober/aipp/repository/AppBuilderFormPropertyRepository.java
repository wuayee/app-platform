/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderFormPropertyRepository {
    List<AppBuilderFormProperty> selectWithFormId(String formId);

    AppBuilderFormProperty selectWithId(String id);

    void insertOne(AppBuilderFormProperty appBuilderFormProperty);

    void insertMore(List<AppBuilderFormProperty> appBuilderFormProperties);

    void updateOne(AppBuilderFormProperty appBuilderFormProperty);

    int deleteMore(List<String> ids);

    void deleteByFormId(String id);
}
