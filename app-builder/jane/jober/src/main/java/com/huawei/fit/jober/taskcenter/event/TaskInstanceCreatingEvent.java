/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;

/**
 * 任务实例创建事件。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-23
 */
public class TaskInstanceCreatingEvent extends TaskInstanceDeclaringEvent {
    private final String instanceId;

    public TaskInstanceCreatingEvent(Object publisher, TaskEntity task, String instanceId, OperationContext context) {
        super(publisher, task, context);
        this.instanceId = instanceId;
    }

    public String instanceId() {
        return this.instanceId;
    }
}
