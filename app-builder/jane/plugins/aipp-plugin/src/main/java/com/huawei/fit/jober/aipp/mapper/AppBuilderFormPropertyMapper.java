/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.AppBuilderFormPropertyPo;

import java.util.List;

/**
 * Aipp表单属性映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderFormPropertyMapper {
    /**
     * 通过表单id查询表单属性
     *
     * @param formId 要查询表单属性的表单id
     * @return 表单属性信息集合
     */
    List<AppBuilderFormPropertyPo> selectWithFormId(String formId);

    /**
     * 根据表单属性id查询表单属性
     *
     * @param id 要查询的表单属性的id
     * @return 表单属性信息
     */
    AppBuilderFormPropertyPo selectWithId(String id);

    /**
     * 插入一条表单属性
     *
     * @param insert 要插入的表单属性
     */
    void insertOne(AppBuilderFormPropertyPo insert);

    /**
     * 插入多条表单属性
     *
     * @param jadeFormProperties 要插入的表单属性的集合
     */
    void insertMore(List<AppBuilderFormPropertyPo> jadeFormProperties);

    /**
     * 更新一条表单属性
     *
     * @param update 要更新的表单属性
     */
    void updateOne(AppBuilderFormPropertyPo update);

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
     * @param formIds 被删除的表单属性的表单id集合
     */
    void deleteByFormIds(List<String> formIds);
}
