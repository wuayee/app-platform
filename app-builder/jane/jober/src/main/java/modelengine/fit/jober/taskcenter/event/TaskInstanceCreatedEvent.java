/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.event;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
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
