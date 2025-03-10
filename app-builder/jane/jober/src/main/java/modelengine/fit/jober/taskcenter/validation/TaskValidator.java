/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

import modelengine.fit.jane.task.util.OperationContext;

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
