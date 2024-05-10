/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;

/**
 * 为任务树提供校验。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-17
 */
public interface TreeValidator {
    /**
     * 校验任务树的名称。
     *
     * @param name 表示任务树的名称的 {@link String}。
     * @param context context
     * @return 表示符合校验逻辑的任务树的名称的 {@link String}。
     */
    String name(String name, OperationContext context);

    /**
     * 校验任务树关联的任务定义的唯一标识。
     *
     * @param taskId 表示任务定义的唯一标识的 {@link String}。
     * @param context context
     * @return 表示符合校验逻辑的任务定义唯一标识的 {@link String}。
     */
    String taskId(String taskId, OperationContext context);
}
