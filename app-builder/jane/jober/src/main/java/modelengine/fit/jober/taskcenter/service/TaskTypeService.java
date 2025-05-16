/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.declaration.NodeDeclaration;
import modelengine.fit.jober.taskcenter.domain.TaskType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 为任务类型提供管理。
 *
 * @author 梁济时
 * @since 2023-09-12
 */
public interface TaskTypeService {
    /**
     * create
     *
     * @param declaration declaration
     * @param context context
     * @return TaskType
     */
    TaskType create(NodeDeclaration declaration, OperationContext context);

    /**
     * 列出指定任务定义中的所有任务类型。
     *
     * @param taskIds 表示待列出任务类型的任务定义的唯一标识的集合 {@link Collection}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示以任务定义唯一标识为键的任务类型的集合的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link TaskType}{@code >>}。
     */
    Map<String, List<TaskType>> listByTasks(Collection<String> taskIds, OperationContext context);
}
