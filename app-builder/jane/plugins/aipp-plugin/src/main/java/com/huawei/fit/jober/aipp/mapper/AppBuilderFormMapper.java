/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderFormPO;

import java.util.List;

/**
 * 表单数据库服务
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-16
 */
public interface AppBuilderFormMapper {
    AppBuilderFormPO selectWithId(String id);

    void insertOne(AppBuilderFormPO insert);

    void updateOne(AppBuilderFormPO update);

    /**
     * 根据类型和租户查询表单
     *
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单结构体
     */
    List<AppBuilderFormPO> selectWithType(String type, String tenantId);

    void delete(String id);
}
