/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.events;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import modelengine.fitframework.event.Event;

import lombok.Getter;

import java.util.List;

/**
 * 回调函数事件类
 *
 * @author 李哲峰
 * @since 1.0
 */
@Getter
public class FlowCallbackEvent<O> implements Event {
    private final List<FlowContext<O>> flowContexts;

    private final Object publisher;

    public FlowCallbackEvent(List<FlowContext<O>> flowContexts, Object publisher) {
        this.flowContexts = flowContexts;
        this.publisher = publisher;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }
}
