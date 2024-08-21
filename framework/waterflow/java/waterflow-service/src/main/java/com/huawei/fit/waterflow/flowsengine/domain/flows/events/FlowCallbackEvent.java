/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.events;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fitframework.event.Event;

import lombok.Getter;

import java.util.List;

/**
 * 回调函数事件类
 *
 * @author 李哲峰
 * @since 2023/12/12
 */
@Getter
public class FlowCallbackEvent<O> implements Event {
    private final List<FlowContext<O>> flowContexts;

    private final FlowCallback callback;

    private final Object publisher;

    public FlowCallbackEvent(List<FlowContext<O>> flowContexts, FlowCallback callback, Object publisher) {
        this.flowContexts = flowContexts;
        this.callback = callback;
        this.publisher = publisher;
    }

    @Override
    public Object publisher() {
        return this.publisher;
    }
}
