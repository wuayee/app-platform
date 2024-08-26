/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.event;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.dataengine.rest.response.TaskInstanceMetaData;

import modelengine.fitframework.event.Event;

/**
 * 第三方数据TaskInstanceMetaDataEvent
 *
 * @author 晏钰坤
 * @since 2023/6/21
 */
public class TaskInstanceMetaDataEvent implements Event {
    private final Object publisher;

    private final TaskInstanceMetaData data;

    private final OperationContext context;

    public TaskInstanceMetaDataEvent(Object publisher, TaskInstanceMetaData data, OperationContext context) {
        this.publisher = publisher;
        this.data = data;
        this.context = context;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }

    public TaskInstanceMetaData data() {
        return this.data;
    }

    public OperationContext context() {
        return context;
    }
}
