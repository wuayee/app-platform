/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.domain.TaskProperty;

/**
 * 为任务属性被删除的事件提供数据。
 *
 * @author 梁济时
 * @since 2024-01-30
 */
public class TaskPropertyDeletingEvent extends TaskPropertyEvent {
    public TaskPropertyDeletingEvent(Object publisher, TaskProperty property) {
        super(publisher, property);
    }
}
