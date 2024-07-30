/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.event;

import com.huawei.fit.jane.task.domain.TaskProperty;

/**
 * 为任务属性正在被修改的事件提供数据。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-31
 */
public class TaskPropertyModifyingEvent extends TaskPropertyEvent {
    private final TaskProperty.Declaration declaration;

    /**
     * 构造一个新的任务属性正在被修改的事件。
     *
     * @param publisher 发布这个事件的对象。
     * @param property 正在被修改的任务属性。
     * @param declaration 导致变化的属性声明。
     */
    public TaskPropertyModifyingEvent(Object publisher, TaskProperty property, TaskProperty.Declaration declaration) {
        super(publisher, property);
        this.declaration = declaration;
    }

    /**
     * 获取导致变化的属性声明。
     *
     * @return 表示属性声明的 {@link TaskProperty.Declaration}。
     */
    public TaskProperty.Declaration declaration() {
        return this.declaration;
    }
}
