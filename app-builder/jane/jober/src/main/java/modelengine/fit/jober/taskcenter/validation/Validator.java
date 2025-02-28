/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

/**
 * 任务中心校验器统一接口。
 *
 * @author 陈镕希
 * @since 2023-08-15
 */
public interface Validator {
    /**
     * 对租户唯一标识进行校验。
     *
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示通过校验的租户唯一标识的 {@link String}。
     */
    String validateTenantId(String tenantId);

    /**
     * 对分页查询进行校验。
     *
     * @param offset offset
     * @param limit limit
     */
    void validatePagination(long offset, int limit);
}
