/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.mapper;

import com.huawei.fit.dynamicform.condition.FormQueryCondition;
import com.huawei.fit.dynamicform.condition.PaginationCondition;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单数据库操作接口
 *
 * @author x00649642
 * @since 2023/12/12
 */
public interface FormMapper {
    /**
     * 计算满足条件的数量
     *
     * @param tenantId 租户id
     * @param cond 条件
     * @return 满足条件的数量
     */
    long countWithCondition(@Param("tenantId") String tenantId, @Param("cond") FormQueryCondition cond);

    /**
     * 返回满足条件的动态表单
     *
     * @param tenantId 租户id
     * @param cond 条件
     * @param page 页码
     * @return 满足条件的动态表单
     */
    List<DynamicFormEntity> selectWithCondition(@Param("tenantId") String tenantId,
            @Param("cond") FormQueryCondition cond, @Param("page") PaginationCondition page);

    /**
     * 通过id查询动态表单
     *
     * @param id 主键id
     * @param version 版本
     * @return 动态表单
     */
    DynamicFormEntity selectByPrimaryKey(@Param("id") String id, @Param("version") String version);

    /**
     * 批量查询
     *
     * @param parameters 参数列表
     * @return 动态表单
     */
    List<DynamicFormEntity> selectFormByPrimaryKeyList(@Param("parameters") List<FormMetaQueryParameter> parameters);

    /**
     * 更新或插入动态表单实体
     *
     * @param form 动态表单
     */
    void insertOrUpdateByPrimaryKey(DynamicFormEntity form);

    /**
     * 删除
     *
     * @param id 主键id
     * @param version 版本
     */
    void deleteByPrimaryKey(@Param("id") String id, @Param("version") String version);
}