/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.bff.service.a3000;

import com.huawei.fit.waterflow.flowsengine.domain.flows.InterStream;
import com.huawei.fit.waterflow.flowsengine.domain.flows.InterStreamHandler;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;

import java.util.HashSet;
import java.util.Set;

/**
 * 接收外部数据，用于和任务流程中指定的节点关联数据通道
 *
 * @author s00558940
 * @since 2024/2/19
 */
public class AppendStream implements InterStream<FlowData> {
    private Set<InterStreamHandler> listeners = new HashSet<>();

    @Override
    public void register(InterStreamHandler<FlowData> handler) {
        this.listeners.add(handler);
    }

    @Override
    public void publish(FlowData data, String id) {
        this.listeners.forEach(s -> s.handle(data, id));
    }

    @Override
    public void publish(FlowData[] data, String id) {
        this.listeners.forEach(s -> s.handle(data, id));
    }
}
