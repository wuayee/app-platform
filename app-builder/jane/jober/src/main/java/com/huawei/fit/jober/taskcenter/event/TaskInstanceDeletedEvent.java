/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;

/**
 * 当任务实例被创建时引发的事件。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-04
 */
public class TaskInstanceDeletedEvent extends TaskInstanceEvent {
    public TaskInstanceDeletedEvent(Object publisher, TaskEntity task, TaskInstance instance,
            OperationContext context) {
        super(publisher, task, instance, context);
    }
}
