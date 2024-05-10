/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fitframework.event.Event;

/**
 * 任务实例事件声明基类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-23
 */
public class TaskInstanceDeclaringEvent implements Event {
    private final Object publisher;

    private final TaskEntity task;

    private final OperationContext context;

    private TaskInstance.Declaration declaration;

    public TaskInstanceDeclaringEvent(Object publisher, TaskEntity task, OperationContext context) {
        this.publisher = publisher;
        this.task = task;
        this.context = context;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }

    public void declaration(TaskInstance.Declaration declaration) {
        this.declaration = declaration;
    }

    public TaskInstance.Declaration declaration() {
        return this.declaration;
    }

    public TaskEntity task() {
        return this.task;
    }

    public OperationContext context() {
        return this.context;
    }
}