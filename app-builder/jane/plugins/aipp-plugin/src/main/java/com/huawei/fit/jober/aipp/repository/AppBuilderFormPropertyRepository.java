/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.repository;

import com.huawei.fit.jober.aipp.domain.AppBuilderFormProperty;

import java.util.List;

/**
 * AppBuilder表单属性持久化层
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderFormPropertyRepository {
    /**
     * 通过表单id查询表单属性
     *
     * @param formId 要查询表单属性的表单id
     * @return 表单属性信息集合
     */
    List<AppBuilderFormProperty> selectWithFormId(String formId);

    /**
     * 根据表单属性id查询表单属性
     *
     * @param id 要查询的表单属性的id
     * @return 表单属性信息
     */
    AppBuilderFormProperty selectWithId(String id);

    /**
     * 插入一条表单属性
     *
     * @param appBuilderFormProperty 要插入的表单属性
     */
    void insertOne(AppBuilderFormProperty appBuilderFormProperty);

    /**
     * 插入多条表单属性
     *
     * @param appBuilderFormProperties 要插入的表单属性的集合
     */
    void insertMore(List<AppBuilderFormProperty> appBuilderFormProperties);

    /**
     * 更新一条表单属性
     *
     * @param appBuilderFormProperty 要更新的表单属性
     */
    void updateOne(AppBuilderFormProperty appBuilderFormProperty);

    /**
     * 通过表单属性id删除多条表单属性
     *
     * @param ids 被删除的表单属性id的集合
     * @return 被删除的表单属性的数量
     */
    int deleteMore(List<String> ids);

    /**
     * 通过表单id删除表单属性
     *
     * @param id 被删除的表单属性的表单id
     */
    void deleteByFormId(String id);
}
