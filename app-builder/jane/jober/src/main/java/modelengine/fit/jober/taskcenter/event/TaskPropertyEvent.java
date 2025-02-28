/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.event;

import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fitframework.event.Event;

/**
 * 为任务属性相关的事件提供数据。
 *
 * @author 梁济时
 * @since 2024-01-09
 */
public class TaskPropertyEvent implements Event {
    private final Object publisher;

    private final TaskProperty property;

    public TaskPropertyEvent(Object publisher, TaskProperty property) {
        this.publisher = publisher;
        this.property = property;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }

    /**
     * 获取与事件相关的任务属性。
     *
     * @return 表示任务属性的 {@link TaskProperty}。
     */
    public TaskProperty property() {
        return this.property;
    }
}
