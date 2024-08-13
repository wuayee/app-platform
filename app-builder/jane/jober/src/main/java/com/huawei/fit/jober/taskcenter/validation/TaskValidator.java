/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;

/**
 * 为任务定义提供校验器。
 *
 * @author 梁致强
 * @since 2023-08-17
 */
public interface TaskValidator extends Validator {
    /**
     * 对任务定义的名称进行校验。
     *
     * @param name 表示任务定义名称的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务定义名称的 {@link String}。
     */
    String validateName(String name, OperationContext context);

    /**
     * 对任务唯一标识进行校验。
     *
     * @param taskId 表示任务唯一标识的 {@link String}。
     * @param context context
     * @return 表示通过校验的任务唯一标识的 {@link String}。
     */
    String validateTaskId(String taskId, OperationContext context);
}
