/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.entity.task.Task;
import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 任务实例变化Genericable。
 *
 * @author 陈镕希
 * @since 2023-09-08
 */
public interface InstanceChangedService {
    /**
     * 新的任务实例被创建。
     *
     * @param task 表示被创建的任务实例所属的任务定义的 {@link Task}。
     * @param instance 表示被创建的任务实例的 {@link Instance}。
     * @param context 表示创建任务的操作人上下文的 {@link OperationContext}。
     */
    @Genericable(id = "e1c7adbb69f148c3b81d0067ad02799f")
    void create(Task task, Instance instance, OperationContext context);

    /**
     * 已有的任务实例被更新。
     *
     * @param task 表示被更新的任务实例所属的任务定义的 {@link Task}。
     * @param instance 表示被更新的任务实例的 {@link Instance}。
     * @param values 表示被更新的key与更新前值的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param context 表示更新任务的操作人上下文的 {@link OperationContext}。
     */
    @Genericable(id = "59e2aeaeffad4242bf8c446be11d20e6")
    void update(Task task, Instance instance, Map<String, Object> values, OperationContext context);

    /**
     * 已有的任务实例被删除。
     *
     * @param task 表示被删除的任务实例所属的任务定义的 {@link Task}。
     * @param instance 表示被删除的任务实例的 {@link Instance}。
     * @param context 表示删除任务的操作人上下文的 {@link OperationContext}。
     */
    @Genericable(id = "47227b3e78924058b428f9a125938d59")
    void delete(Task task, Instance instance, OperationContext context);
}
