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
    PageResponse<DynamicFormEntity> queryFormWithCondition(String tenantId, FormQueryCondition cond,
            PaginationCondition page);

    PageResponse<DynamicFormEntity> queryFormWithCondition(FormQueryCondition cond, PaginationCondition page,
            OperationContext context);

    DynamicFormDetailEntity queryFormDetailByPrimaryKey(String formId, String version, OperationContext context);

    List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> queryFormDetailByPrimaryKeyAndMap(
            List<FormMetaQueryParameter> parameters, OperationContext context);

    boolean saveForm(DynamicFormDetailEntity formDetail, OperationContext context);

    boolean deleteForm(DynamicFormDto formDto, OperationContext context);
}
