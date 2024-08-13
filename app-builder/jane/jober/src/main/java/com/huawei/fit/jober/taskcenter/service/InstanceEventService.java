/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.declaration.InstanceEventDeclaration;
import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;

import java.util.List;
import java.util.Map;

/**
 * 为实例事件提供管理。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
public interface InstanceEventService {
    /**
     * 保存实例事件。
     *
     * @param declarations 表示实例事件声明的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link InstanceEventDeclaration}{@code >>}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    void save(Map<String, List<InstanceEventDeclaration>> declarations, OperationContext context);

    /**
     * 获取指定任务数据源中定义的事件。
     *
     * @param taskSourceIds 表示任务数据源的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示实例事件的列表的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
     */
    Map<String, List<InstanceEvent>> lookupByTaskSources(List<String> taskSourceIds);

    /**
     * 获取指定任务类型下的实例事件。
     *
     * @param taskTypeId 表示任务类型的唯一标识的 {@link String}。
     * @return 表示实例事件的列表的
     * {@link Map}{@code <}{@link String}{@code , }{@link List}{@code <}{@link String}{@code >>}。
     */
    Map<String, List<InstanceEvent>> lookupByTaskType(String taskTypeId);

    /**
     * 删除指定任务数据中的事件。
     *
     * @param taskSourceIds 表示任务数据源的唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void deleteByTaskSources(List<String> taskSourceIds);

    /**
     * 删除指定任务类型下的实例事件。
     *
     * @param taskTypeId 表示任务类型的唯一标识的 {@link String}。
     */
    void deleteByTaskType(String taskTypeId);
}
