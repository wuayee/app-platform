/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;

import modelengine.fitframework.model.RangedResultSet;

import java.util.List;
import java.util.Map;

/**
 * 为任务数据源提供管理。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public interface SourceService {
    /**
     * 创建任务数据源。
     *
     * @param taskId 表示数据源所属任务的唯一标识的 {@link String}。
     * @param typeId 表示数据源所属任务类型的唯一标识的 {@link String}。
     * @param declaration 表示任务数据源声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务数据源的 {@link SourceEntity}。
     */
    SourceEntity create(String taskId, String typeId, SourceDeclaration declaration, OperationContext context);

    /**
     * 更新任务数据源。
     *
     * @param taskId 表示数据源所属任务的唯一标识的 {@link String}。
     * @param typeId 表示数据源所属任务类型的唯一标识的 {@link String}。
     * @param sourceId 表示待更新的任务数据源的唯一标识的 {@link String}。
     * @param declaration 表示任务数据源声明的 {@link SourceDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void patch(String taskId, String typeId, String sourceId, SourceDeclaration declaration, OperationContext context);

    /**
     * 删除指定任务数据源。
     *
     * @param taskId 表示数据源所属任务的唯一标识的 {@link String}。
     * @param typeId 表示数据源所属任务类型的唯一标识的 {@link String}。
     * @param sourceId 表示待删除的任务数据源的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void delete(String taskId, String typeId, String sourceId, OperationContext context);

    /**
     * 检索指定任务数据源。
     *
     * @param taskId 表示数据源所属任务的唯一标识的 {@link String}。
     * @param typeId 表示数据源所属任务类型的唯一标识的 {@link String}。
     * @param sourceId 表示待检索的任务数据源的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示该任务数据源的 {@link SourceEntity}。
     */
    SourceEntity retrieve(String taskId, String typeId, String sourceId, OperationContext context);

    /**
     * 列出指定类型下所有的任务数据源。
     *
     * @param taskId 表示数据源所属任务的唯一标识的 {@link String}。
     * @param typeId 表示数据源所属任务类型的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示该任务类型下所有的任务数据源的列表的 {@link List}{@code <}{@link SourceEntity}{@code >}。
     */
    List<SourceEntity> list(String taskId, String typeId, OperationContext context);

    /**
     * 列出指定任务定义的数据源。
     *
     * @param taskIds 表示待查询的任务数据源所在的任务定义的唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示该任务的 {@code <}任务唯一标识{@code ,}数据源列表{@code >}的
     * {@link Map}{@code <}{@link String}{@code ,}{@link List}{@code <}{@link SourceEntity}{@code >}{@code >}。
     */
    Map<String, List<SourceEntity>> list(List<String> taskIds, OperationContext context);

    /**
     * 根据唯一标识列出指定的数据源。
     *
     * @param sourceIds 表示待查询的任务数据源唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param offset 表示待查询的结果集在所有符合条件的结果集中的偏移量的 64 位整数。
     * @param limit 表示待查询的结果集中允许包含结果的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示该任务的数据源列表的 {@link RangedResultSet}{@code <}{@link SourceEntity}{@code >}。
     */
    RangedResultSet<SourceEntity> listBySourceIds(List<String> sourceIds, long offset, int limit,
            OperationContext context);
}
