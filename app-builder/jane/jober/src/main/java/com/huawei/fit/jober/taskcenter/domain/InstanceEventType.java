/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

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
