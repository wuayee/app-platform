/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;

/**
 * 为任务属性被取消索引的事件提供数据。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-09
 */
public class TaskPropertyUnindexedEvent extends TaskPropertyEvent {
    private final TaskEntity task;

    /**
     * 构造一个表示任务属性被取消索引的事件的新实例。
     *
     * @param publisher 发布此事件的对象。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param property 表示任务属性的 {@link TaskProperty}。
     */
    public TaskPropertyUnindexedEvent(Object publisher, TaskEntity task, TaskProperty property) {
        super(publisher, property);
        this.task = task;
    }

    /**
     * 获取事件相关的属性所属的任务定义。
     *
     * @return 表示任务定义的 {@link TaskEntity}。
     */
    public TaskEntity task() {
        return this.task;
    }
}
