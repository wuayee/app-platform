/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.event;

import modelengine.fit.jane.task.domain.TaskProperty;

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
