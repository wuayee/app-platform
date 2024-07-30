/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.dynamicform;

import com.huawei.fit.dynamicform.common.PageResponse;
import com.huawei.fit.dynamicform.condition.FormQueryCondition;
import com.huawei.fit.dynamicform.condition.PaginationCondition;
import com.huawei.fit.dynamicform.dto.DynamicFormDto;
import com.huawei.fit.dynamicform.entity.DynamicFormDetailEntity;
import com.huawei.fit.dynamicform.entity.DynamicFormEntity;
import com.huawei.fit.dynamicform.entity.FormMetaQueryParameter;
import com.huawei.fit.jane.common.entity.OperationContext;

import java.util.List;
import java.util.Map;

/**
 * form 服务层接口
 *
 * @author x00649642
 * @since 2023-12-12
 */
public interface DynamicFormService {
    /**
     * 根据租户id和条件查询表单
     *
     * @param tenantId 表示租户id
     * @param cond 表示查询条件
     * @param page 表示分页信息
     * @return 查询得到的表单实体
     */
    PageResponse<DynamicFormEntity> queryFormWithCondition(String tenantId, FormQueryCondition cond,
            PaginationCondition page);

    /**
     * 根据用户信息和条件查询表单
     *
     * @param cond 表示条件
     * @param page 表示分页信息
     * @param context 表示用户信息上下文
     * @return 查询得到的表单实体
     */
    PageResponse<DynamicFormEntity> queryFormWithCondition(FormQueryCondition cond, PaginationCondition page,
            OperationContext context);

    /**
     * 根据主键查询表单详情
     *
     * @param formId 表示表单id
     * @param version 表示表单版本
     * @param context 表示用户信息上下文
     * @return 表单详情实体
     */
    DynamicFormDetailEntity queryFormDetailByPrimaryKey(String formId, String version, OperationContext context);

    /**
     * 根据主键查询表单map
     *
     * @param parameters 表示参数列表
     * @param context 表示用户信息上下文
     * @return 查询得到的表单map
     */
    List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> queryFormDetailByPrimaryKeyAndMap(
            List<FormMetaQueryParameter> parameters, OperationContext context);

    /**
     * 保存表单
     *
     * @param formDetail 表示表单详情
     * @param context 表示用户信息上下文
     * @return 保存结果
     */
    boolean saveForm(DynamicFormDetailEntity formDetail, OperationContext context);

    /**
     * 删除表单
     *
     * @param formDto 表示表单实体
     * @param context 表示用户信息上下文
     * @return 删除结果
     */
    boolean deleteForm(DynamicFormDto formDto, OperationContext context);
}
