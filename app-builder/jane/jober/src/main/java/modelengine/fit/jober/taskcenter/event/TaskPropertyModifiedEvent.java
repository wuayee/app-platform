/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.event;

import modelengine.fit.jane.task.domain.TaskProperty;

/**
 * 当属性被修改后引发的事件。
 *
 * @author 梁济时
 * @since 2024-01-31
 */
public class TaskPropertyModifiedEvent extends TaskPropertyEvent {
    private final TaskProperty oldProperty;

    /**
     * 构造一个新的任务属性修改事件。
     *
     * @param publisher 发布这个事件的对象。
     * @param property 修改后的任务属性。
     * @param oldProperty 修改前的任务属性。
     */
    public TaskPropertyModifiedEvent(Object publisher, TaskProperty property, TaskProperty oldProperty) {
        super(publisher, property);
        this.oldProperty = oldProperty;
    }

    /**
     * 获取修改前的任务属性。
     *
     * @return 表示任务属性的 {@link TaskProperty}。
     */
    public TaskProperty oldProperty() {
        return this.oldProperty;
    }
}
