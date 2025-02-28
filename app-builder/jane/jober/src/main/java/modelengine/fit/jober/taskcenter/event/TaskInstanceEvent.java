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
 * 当任务实例事件基类。
 *
 * @author 陈镕希
 * @since 2023-10-11
 */
public class TaskInstanceEvent implements Event {
    private final Object publisher;

    private final TaskEntity task;

    private final TaskInstance instance;

    private final OperationContext context;

    public TaskInstanceEvent(Object publisher, TaskEntity task, TaskInstance instance, OperationContext context) {
        this.publisher = publisher;
        this.task = task;
        this.instance = instance;
        this.context = context;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }

    /**
     * 获取被创建的实例所属的任务。
     *
     * @return 表示任务的 {@link TaskEntity}。
     */
    public TaskEntity task() {
        return this.task;
    }

    /**
     * 获取新创建的任务实例。
     *
     * @return 表示新创建的任务实例的 {@link TaskInstance}。
     */
    public TaskInstance instance() {
        return this.instance;
    }

    /**
     * 获取操作相关上下文。
     *
     * @return 表示操作相关上下文的 {@link OperationContext}。
     */
    public OperationContext context() {
        return this.context;
    }
}
