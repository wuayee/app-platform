/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderForm;

import java.util.List;

/**
 * 表单数据层服务
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderFormRepository {
    /**
     * 根据id查询表单
     *
     * @param id 表单id
     * @return 表单结构体
     */
    AppBuilderForm selectWithId(String id);

    /**
     * 根据类型和租户查询表单
     *
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单结构体
     */
    List<AppBuilderForm> selectWithType(String type, String tenantId);

    /**
     * 插入一个表单
     *
     * @param appBuilderForm 表单结构体
     */
    void insertOne(AppBuilderForm appBuilderForm);

    /**
     * 更新表单
     *
     * @param appBuilderForm 表单结构体
     */
    void updateOne(AppBuilderForm appBuilderForm);

    /**
     * 根据id删除表单
     *
     * @param ids 表单id集合
     */
    void delete(List<String> ids);
}
