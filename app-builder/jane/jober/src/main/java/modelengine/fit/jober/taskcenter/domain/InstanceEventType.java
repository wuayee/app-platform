/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

/**
 * 表示实例事件的类型。
 *
 * @author 梁济时
 * @since 2023-09-04
 */
public enum InstanceEventType {
    /**
     * 表示当任务实例被创建时引发的事件。
     */
    CREATED,

    /**
     * 表示当任务实例被修改时引发的事件。
     */
    MODIFIED,

    /**
     * 表示当任务实例被删除时引发的事件。
     */
    DELETED,
}
