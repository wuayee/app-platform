/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultTaskInstanceEventNotifier;
import com.huawei.fitframework.plugin.Plugin;

/**
 * 为任务实例的变更提供事件触发器。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-10
 */
public interface TaskInstanceEventNotifier extends Runnable {
    TaskInstanceEventNotifier noticeOld(TaskInstance... instances);

    TaskInstanceEventNotifier noticeNew(TaskInstance... instances);

    static TaskInstanceEventNotifier custom(Plugin plugin, TaskEntity task, OperationContext context) {
        return new DefaultTaskInstanceEventNotifier(plugin, task, context);
    }
}
