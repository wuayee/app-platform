/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;
import com.huawei.fitframework.model.RangedResultSet;

import java.util.List;

/**
 * 为任务定义提供管理。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-08
 */
public interface TaskService {
    /**
     * 创建任务定义。
     *
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务定义的 {@link TaskEntity}。
     */
    TaskEntity create(TaskDeclaration declaration, OperationContext context);

    /**
     * 更新任务定义。
     *
     * @param taskId 表示待更新的任务定义的唯一标识的 {@link String}。
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patch(String taskId, TaskDeclaration declaration, OperationContext context);

    /**
     * 删除任务定义。
     *
     * @param taskId 表示待删除的任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void delete(String taskId, OperationContext context);

    /**
     * 检索任务定义。
     *
     * @param taskId 表示待检索的任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务定义的 {@link TaskEntity}。
     */
    TaskEntity retrieve(String taskId, OperationContext context);

    /**
     * 查询任务定义。
     *
     * @param filter 表示任务过滤器的 {@link TaskFilter}。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskEntity}{@code >}。
     */
    RangedResultSet<TaskEntity> list(TaskFilter filter, long offset, int limit, OperationContext context);

    /**
     * 查询任务定义。
     *
     * @param filter 表示任务过滤器的 {@link TaskFilter}。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskEntity}{@code >}。
     */
    RangedResultSet<TaskEntity> listForApplication(TaskFilter filter, long offset, int limit, OperationContext context);

    /**
     * 查询任务定义为多版本Meta
     *
     * @param filter 查询条件过滤器 {@link MetaFilter}。
     * @param isLatestOnly 是否仅查询最新版本的boolean。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskEntity}{@code >}。
     */
    RangedResultSet<TaskEntity> listMeta(MetaFilter filter, boolean isLatestOnly, long offset, int limit,
            OperationContext context);

    /**
     * 检索任务定义。
     *
     * @param taskIds 表示待检索的任务定义的唯一标识的 {@link List<String> }。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务定义的 {@link List<TaskEntity>}。
     */
    List<TaskEntity> listTaskEntities(List<String> taskIds, OperationContext context);
}
