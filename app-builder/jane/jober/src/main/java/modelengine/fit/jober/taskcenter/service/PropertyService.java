/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.declaration.TaskPropertiesDeclaration;

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
