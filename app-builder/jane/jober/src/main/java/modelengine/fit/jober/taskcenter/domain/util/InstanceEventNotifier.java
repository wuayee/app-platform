/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.util.support.DefaultInstanceEventNotifier;
import modelengine.fitframework.plugin.Plugin;

/**
 * 为实例事件提供通知程序。
 *
 * @author 梁济时
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
