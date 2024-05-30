/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;

import java.util.List;

/**
 * 表单数据层服务
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderFormRepository {
    AppBuilderForm selectWithId(String id);

    /**
     * 根据类型和租户查询表单
     *
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单结构体
     */
    List<AppBuilderForm> selectWithType(String type, String tenantId);

    void insertOne(AppBuilderForm appBuilderForm);

    void updateOne(AppBuilderForm appBuilderForm);

    void delete(String id);
}
