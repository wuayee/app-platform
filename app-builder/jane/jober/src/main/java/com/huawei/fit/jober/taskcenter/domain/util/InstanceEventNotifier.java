/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.support.DefaultInstanceEventNotifier;
import com.huawei.fitframework.plugin.Plugin;

/**
 * 为实例事件提供通知程序。
 *
 * @author 梁济时 l00815032
 * @since 2023-09-05
 */
public interface InstanceEventNotifier extends Runnable {
    /**
     * noticeOld
     *
     * @param instances instances
     * @return InstanceEventNotifier
     */
    InstanceEventNotifier noticeOld(TaskInstance... instances);

    /**
     * noticeNew
     *
     * @param instances instances
     * @return InstanceEventNotifier
     */
    InstanceEventNotifier noticeNew(TaskInstance... instances);

    /**
     * custom
     *
     * @param task task
     * @param plugin plugin
     * @param context context
     * @return InstanceEventNotifier
     */
    static InstanceEventNotifier custom(TaskEntity task, Plugin plugin, OperationContext context) {
        return new DefaultInstanceEventNotifier(task, plugin, context);
    }
}
