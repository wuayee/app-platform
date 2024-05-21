/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.dto.aipplog.AppQueryCondition;
import com.huawei.fit.jober.aipp.po.AppBuilderAppPO;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderAppMapper {
    AppBuilderAppPO selectWithId(String id);

    List<AppBuilderAppPO> selectWithTenantId(String tenantId);

    List<AppBuilderAppPO> selectByTenantIdWithPage(String tenantId, String typeFilter, long offset, int limit);

    List<AppBuilderAppPO> selectWithCondition(AppQueryCondition cond);

    long countByTenantId(String tenantId, String typeFilter);

    void insertOne(AppBuilderAppPO insert);

    void updateOne(AppBuilderAppPO update);

    List<AppBuilderAppPO> selectWithStoreId(List<String> storeIds);

    void updateAppWithStoreId(String uniqueName, String id, String version);

    void delete(String appId);
}
