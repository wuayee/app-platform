/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fitframework.log.Logger;

import java.util.List;

/**
 * 异步化节点间操作内存版实现类
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class FlowContextMemoMessenger implements FlowContextMessenger {
    private static final Logger log = Logger.get(FlowContextMemoMessenger.class);

    @Override
    public <I> void send(String nodeId, List<FlowContext<I>> contexts) {
        log.warn("FlowEngine memo messenger does not support sending events.");
    }

    @Override
    public <I> void sendCallback(FlowCallback callback, List<FlowContext<I>> contexts) {
        log.warn("FlowEngine memo messenger does not support sending events.");
    }
}
