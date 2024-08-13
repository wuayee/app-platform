/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.TaskPropertiesDeclaration;

/**
 * 为任务属性提供管理。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public interface PropertyService {
    /**
     * 批量保存任务属性。
     *
     * @param declaration 表示任务属性声明的 {@link TaskPropertiesDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void batchSave(TaskPropertiesDeclaration declaration, OperationContext context);
}
