/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.util.support;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.util.TaskInstanceEventNotifier;
import modelengine.fit.jober.taskcenter.event.TaskInstanceCreatedEvent;
import modelengine.fit.jober.taskcenter.event.TaskInstanceDeletedEvent;
import modelengine.fit.jober.taskcenter.event.TaskInstanceModifiedEvent;
import modelengine.fitframework.event.Event;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.util.MapUtils;

import java.util.Optional;

/**
 * 为 {@link TaskInstanceEventNotifier} 提供默认实现。
 *
 * @author 梁济时
 * @since 2024-01-10
 */
public class DefaultTaskInstanceEventNotifier extends AbstractTaskInstanceEventNotifier
        implements TaskInstanceEventNotifier {
    private final TaskEntity task;

    private final OperationContext context;

    /**
     * 构造一个默认的任务实例事件通知器。
     *
     * @param plugin 插件对象，用于发布事件
     * @param task 任务实体，事件关联的任务
     * @param context 操作上下文，事件的上下文信息
     */
    public DefaultTaskInstanceEventNotifier(Plugin plugin, TaskEntity task, OperationContext context) {
        super(plugin);
        this.task = task;
        this.context = context;
    }

    @Override
    public TaskInstanceEventNotifier noticeOld(TaskInstance... instances) {
        this.addOlds(instances);
        return this;
    }

    @Override
    public TaskInstanceEventNotifier noticeNew(TaskInstance... instances) {
        this.addNews(instances);
        return this;
    }

    @Override
    protected Event eventOfCreated(TaskInstance instance) {
        return new TaskInstanceCreatedEvent(this, this.task, instance, this.context);
    }

    @Override
    protected Event eventOfDeleted(TaskInstance instance) {
        return new TaskInstanceDeletedEvent(this, this.task, instance, this.context);
    }

    @Override
    protected Optional<? extends Event> eventOfModified(TaskInstance oldInstance, TaskInstance newInstance) {
        return Optional.of(modifications(oldInstance, newInstance))
                .filter(MapUtils::isNotEmpty)
                .map(oldValues -> new TaskInstanceModifiedEvent(this, this.task, newInstance, oldValues, this.context));
    }
}
