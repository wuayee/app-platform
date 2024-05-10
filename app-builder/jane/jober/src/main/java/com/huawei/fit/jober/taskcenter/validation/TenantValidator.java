/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;

/**
 * 为租户提供校验。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-08
 */
public interface TenantValidator extends Validator {
    /**
     * 校验租户的名称。
     *
     * @param name 表示租户的名称的 {@link String}。
     * @param context context
     * @return 表示符合校验逻辑的租户的名称的 {@link String}。
     */
    String name(String name, OperationContext context);
}
