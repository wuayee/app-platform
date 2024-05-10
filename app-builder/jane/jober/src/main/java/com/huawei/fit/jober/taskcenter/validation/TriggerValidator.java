/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;

/**
 * 为任务属性触发器提供校验器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-18
 */
public interface TriggerValidator {
    /**
     * 对任务属性触发器唯一标识进行校验。
     *
     * @param triggerId 表示任务触发器唯一标识的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务触发器唯一标识的 {@link String}。
     */
    String validateTriggerId(String triggerId, OperationContext context);

    /**
     * 对任务唯一标识进行校验。
     *
     * @param taskId 表示任务唯一标识的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务唯一标识的 {@link String}。
     */
    String validateTaskId(String taskId, OperationContext context);

    /**
     * 验证属性名称
     *
     * @param propertyName 表示属性名称 {@link String}。
     * @param context context
     * @return 表示通过校验的属性名称 {@link String}。
     */
    String validatePropertyName(String propertyName, OperationContext context);

    /**
     * 验证fitableId
     *
     * @param fitableId 表示触发器的fitableId的 {@link String}。
     * @param context context
     * @return 表示通过校验额fitableId的 {@link String}。
     */
    String validateFitableId(String fitableId, OperationContext context);
}
