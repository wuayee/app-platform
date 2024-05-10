/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderApp;
import com.huawei.fit.jober.aipp.dto.aipplog.AppQueryCondition;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderAppRepository {
    AppBuilderApp selectWithId(String id);

    List<AppBuilderApp> selectByTenantIdWithPage(String tenantId, String typeFilter, long offset, int limit);

    List<AppBuilderApp> selectWithCondition(AppQueryCondition cond);

    long countByTenantId(String tenantId, String typeFilter);

    void insertOne(AppBuilderApp appBuilderApp);

    void updateOne(AppBuilderApp appBuilderApp);
}
