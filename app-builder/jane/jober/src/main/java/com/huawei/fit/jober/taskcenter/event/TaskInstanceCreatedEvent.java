/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;

import modelengine.fitframework.util.StringUtils;

/**
 * 当任务实例被创建时引发的事件。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
public class TaskInstanceCreatedEvent extends TaskInstanceEvent {
    public TaskInstanceCreatedEvent(Object publisher, TaskEntity task, TaskInstance instance,
            OperationContext context) {
        super(publisher, task, instance, context);
    }

    @Override
    public String toString() {
        return StringUtils.format("[type=TaskInstanceCreated, taskId={0}, instanceId={1}]", this.task().getId(),
                this.instance().id());
    }
}
