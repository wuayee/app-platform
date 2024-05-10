/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;

/**
 * 任务实例更新事件。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-23
 */
public class TaskInstanceModifyingEvent extends TaskInstanceDeclaringEvent {
    private final TaskInstance old;

    public TaskInstanceModifyingEvent(Object publisher, TaskEntity task, OperationContext context, TaskInstance old) {
        super(publisher, task, context);
        this.old = old;
    }

    /**
     * 获取修改前的任务实例信息。
     *
     * @return 表示任务实例的 {@link TaskInstance}。
     */
    public TaskInstance old() {
        return this.old;
    }
}
