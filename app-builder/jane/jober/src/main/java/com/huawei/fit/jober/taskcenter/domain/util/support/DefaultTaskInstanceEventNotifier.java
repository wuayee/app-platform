/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.TaskInstanceEventNotifier;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceCreatedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceDeletedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskInstanceModifiedEvent;
import com.huawei.fitframework.event.Event;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.util.MapUtils;

import java.util.Optional;

/**
 * 为 {@link TaskInstanceEventNotifier} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-10
 */
public class DefaultTaskInstanceEventNotifier extends AbstractTaskInstanceEventNotifier
        implements TaskInstanceEventNotifier {
    private final TaskEntity task;

    private final OperationContext context;

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
