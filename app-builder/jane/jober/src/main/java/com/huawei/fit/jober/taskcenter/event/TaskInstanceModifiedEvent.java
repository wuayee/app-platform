/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;

import java.util.Map;

/**
 * 当任务实例被修改时引发的事件。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-04
 */
public class TaskInstanceModifiedEvent extends TaskInstanceEvent {
    private final Map<String, Object> values;

    /**
     * 构造一个新的任务实例被修改的事件。
     *
     * @param publisher 发布事件的对象。
     * @param task 被修改的任务实例所属的任务。
     * @param instance 被修改的任务实例。
     * @param values 表示被修改的属性的原始值的
     *            {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param context 操作的上下文。
     */
    public TaskInstanceModifiedEvent(Object publisher, TaskEntity task, TaskInstance instance,
            Map<String, Object> values, OperationContext context) {
        super(publisher, task, instance, context);
        this.values = values;
    }

    /**
     * 获取被修改的属性的原始值。
     *
     * @return 表示被修改的属性的原始值的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> values() {
        return this.values;
    }
}
