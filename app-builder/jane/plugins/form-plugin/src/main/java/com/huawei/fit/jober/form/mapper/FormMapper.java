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
    long countWithCondition(@Param("tenantId") String tenantId, @Param("cond") FormQueryCondition cond);

    List<DynamicFormEntity> selectWithCondition(@Param("tenantId") String tenantId,
            @Param("cond") FormQueryCondition cond, @Param("page") PaginationCondition page);

    DynamicFormEntity selectByPrimaryKey(@Param("id") String id, @Param("version") String version);

    List<DynamicFormEntity> selectFormByPrimaryKeyList(
            @Param("parameters") List<FormMetaQueryParameter> parameters);

    void insertOrUpdateByPrimaryKey(DynamicFormEntity form);

    void deleteByPrimaryKey(@Param("id") String id, @Param("version") String version);
}