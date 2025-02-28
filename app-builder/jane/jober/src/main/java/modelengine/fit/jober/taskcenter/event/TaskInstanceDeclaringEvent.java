/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.event;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fitframework.event.Event;

/**
 * 任务实例事件声明基类。
 *
 * @author 陈镕希
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