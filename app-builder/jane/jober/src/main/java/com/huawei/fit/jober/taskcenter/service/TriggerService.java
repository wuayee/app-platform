/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.SourceTriggersDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TriggerDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.filter.TriggerFilter;

import java.util.List;
import java.util.Map;

/**
 * 为任务属性触发器提供管理。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public interface TriggerService {
    /**
     * 创建任务属性触发器。
     *
     * @param taskId 表示待创建的任务属性触发器所在的任务定义的唯一标识的 {@link String}。
     * @param sourceId 表示待创建的任务属性所在的任务数据源的唯一标识的 {@link String}。
     * @param declaration 表示任务属性触发器声明的 {@link TriggerDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务属性触发器的 {@link TriggerEntity}。
     */
    TriggerEntity create(String taskId, String sourceId, TriggerDeclaration declaration, OperationContext context);

    /**
     * 更新任务属性触发器。
     *
     * @param taskId 表示待更新的任务属性触发器所在的任务定义的唯一标识的 {@link String}。
     * @param sourceId 表示待更新的任务属性触发器所在的任务数据源的唯一标识的 {@link String}。
     * @param triggerId 表示待更新的任务属性触发器的唯一标识的 {@link String}。
     * @param declaration 表示任务属性触发器的声明的 {@link TriggerDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patch(String taskId, String sourceId, String triggerId, TriggerDeclaration declaration,
            OperationContext context);

    /**
     * 删除任务属性触发器。
     *
     * @param taskId 表示待删除的任务属性触发器所在的任务定义的唯一标识的 {@link String}。
     * @param filter 表示任务属性触发器过滤器的 {@link TriggerFilter}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void delete(String taskId, TriggerFilter filter, OperationContext context);

    /**
     * 检索任务属性触发器。
     *
     * @param taskId 表示待检索的任务属性触发器所在的任务定义的唯一标识的 {@link String}。
     * @param sourceId 表示待检索的任务属性触发器所在的任务数据源的唯一标识的 {@link String}。
     * @param triggerId 表示待检索的任务属性触发器的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示该任务属性触发器的 {@link TriggerEntity}。
     */
    TriggerEntity retrieve(String taskId, String sourceId, String triggerId, OperationContext context);

    /**
     * 列出指定任务下的任务属性触发器。
     *
     * @param filter 表示任务属性触发器过滤器的 {@link TriggerFilter}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示以所属任务数据源的唯一标识分组的任务属性触发器的。
     */
    Map<String, List<TriggerEntity>> list(TriggerFilter filter, OperationContext context);

    /**
     * 批量保存任务属性触发器。
     *
     * @param taskId 表示待检索的任务属性触发器所在的任务定义的唯一标识的 {@link String}。
     * @param declarations 表示待保存的任务属性触发器的声明的 {@link List}{@code <}{@link SourceTriggersDeclaration}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void batchSave(String taskId, List<SourceTriggersDeclaration> declarations, OperationContext context);
}