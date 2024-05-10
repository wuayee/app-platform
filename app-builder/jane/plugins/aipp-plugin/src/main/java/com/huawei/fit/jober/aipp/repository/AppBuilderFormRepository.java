/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderFormRepository {
    AppBuilderForm selectWithId(String id);

    List<AppBuilderForm> selectWithType(String type);

    void insertOne(AppBuilderForm appBuilderForm);

    void updateOne(AppBuilderForm appBuilderForm);
}
