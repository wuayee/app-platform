/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.event;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;

/**
 * 任务实例创建事件。
 *
 * @author 陈镕希
 * @since 2023-10-23
 */
public class TaskInstanceCreatingEvent extends TaskInstanceDeclaringEvent {
    private final String instanceId;

    /**
     * 任务实例创建事件
     *
     * @param publisher 事件发布者
     * @param task 任务实体
     * @param instanceId 任务实例id
     * @param context 操作上下文
     */
    public TaskInstanceCreatingEvent(Object publisher, TaskEntity task, String instanceId, OperationContext context) {
        super(publisher, task, context);
        this.instanceId = instanceId;
    }

    public String instanceId() {
        return this.instanceId;
    }
}
