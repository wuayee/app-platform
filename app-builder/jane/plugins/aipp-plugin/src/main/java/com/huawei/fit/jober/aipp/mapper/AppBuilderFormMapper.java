/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderFormPo;

import java.util.List;

/**
 * 表单数据库服务
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderFormMapper {
    /**
     * 通过表单id查询表单信息
     *
     * @param id 要查询的表单id
     * @return 表单结构体
     */
    AppBuilderFormPo selectWithId(String id);

    /**
     * 插入一条表单信息
     *
     * @param insert 要插入的表单信息
     */
    void insertOne(AppBuilderFormPo insert);

    /**
     * 更新一条表单信息
     *
     * @param update 被更新的表单信息
     */
    void updateOne(AppBuilderFormPo update);

    /**
     * 根据类型和租户查询表单
     *
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单结构体
     */
    List<AppBuilderFormPo> selectWithType(String type, String tenantId);

    /**
     * 通过表单id删除表单信息
     *
     * @param ids 被删除的表单id
     */
    void delete(List<String> ids);
}
