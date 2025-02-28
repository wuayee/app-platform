/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

import modelengine.fit.jane.task.util.OperationContext;

/**
 * 为租户提供校验。
 *
 * @author 陈镕希
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
