/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.util.OperationContext;

/**
 * 为任务属性提供校验器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-18
 */
public interface PropertyValidator {
    /**
     * 对任务属性唯一标识进行校验。
     *
     * @param propertyId 表示任务数据源唯一标识的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务数据源唯一标识的 {@link String}。
     */
    String validatePropertyId(String propertyId, OperationContext context);

    /**
     * 对任务唯一标识进行校验。
     *
     * @param taskId 表示任务唯一标识的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务唯一标识的 {@link String}。
     */
    String validateTaskId(String taskId, OperationContext context);

    /**
     * 验证任务名称
     *
     * @param name 表示任务属性的名称的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务属性名称的 {@link String}。
     */
    String validateName(String name, OperationContext context);

    /**
     * 验证描述信息
     *
     * @param description 表示任务属性的描述的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务属性的描述的 {@link String}。
     */
    String validateDescription(String description, OperationContext context);

    /**
     * 验证属性的数据类型
     *
     * @param dataType 表示任务属性的数据类型的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务属性的数据类型的 {@link PropertyDataType}。
     */
    PropertyDataType validateDataType(String dataType, OperationContext context);

    /**
     * 校验属性的可识别性。
     *
     * @param isIdentifiable 表示待校验的值的 {@link Boolean}。
     * @return 表示符合预期的值的 {@link Boolean}。
     */
    Boolean validateIdentifiable(Boolean isIdentifiable);

    /**
     * 校验属性的需求
     *
     * @param isRequired 表示任务属性的需求的 {@link Boolean}.
     * @return 表示通过校验的任务属性的需求的 {@link Boolean}.
     */
    Boolean validateRequired(Boolean isRequired);

    /**
     * 校验属性的作用域
     *
     * @param scope 表示任务属性的作用域的 {@link String}.
     * @param context context
     * @return 表示通过校验的任务属性的作用域的 {@link PropertyScope}.
     */
    PropertyScope validateScope(String scope, OperationContext context);

    /**
     * 校验属性的外观
     *
     * @param appearance 表示任务属性的外观的 {@link String}.
     * @return 表示通过校验的任务属性的外观的 {@link String}.
     */
    String validateAppearance(String appearance);
}
