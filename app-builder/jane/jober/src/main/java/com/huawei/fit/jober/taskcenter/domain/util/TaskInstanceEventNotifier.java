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
 * @author 梁济时
 * @since 2024-01-10
 */
public interface TaskInstanceEventNotifier extends Runnable {
    /**
     * 为旧的任务实例变更注册事件触发器。
     *
     * @param instances 需要注册事件触发器的任务实例
     * @return 返回一个新的任务实例事件通知器，用于注册事件触发器
     */
    TaskInstanceEventNotifier noticeOld(TaskInstance... instances);

    /**
     * 为新的任务实例变更注册事件触发器。
     *
     * @param instances 需要注册事件触发器的任务实例
     * @return 返回一个新的任务实例事件通知器，用于注册事件触发器
     */
    TaskInstanceEventNotifier noticeNew(TaskInstance... instances);

    /**
     * 创建一个自定义的任务实例事件通知器。
     *
     * @param plugin 插件对象，用于提供事件触发器的实现
     * @param task 任务实体，用于提供任务实例的相关信息
     * @param context 操作上下文，用于提供操作的相关信息
     * @return 返回一个新的自定义任务实例事件通知器
     */
    static TaskInstanceEventNotifier custom(Plugin plugin, TaskEntity task, OperationContext context) {
        return new DefaultTaskInstanceEventNotifier(plugin, task, context);
    }
}
