/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.context.repo.flowcontext;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fitframework.log.Logger;

import java.util.List;

/**
 * 异步化节点间操作内存版实现类
 *
 * @author g00564732
 * @since 1.0
 */
public class FlowContextMemoMessenger implements FlowContextMessenger {
    private static final Logger log = Logger.get(FlowContextMemoMessenger.class);

    /**
     * 发送事件到引擎外部
     *
     * @param nodeId 节点ID
     * @param contexts 流程实例执行过程产生的contexts
     * @param <I> 流程实例执行时的入参数据类型，用于泛型推倒
     */
    @Override
    public <I> void send(String nodeId, List<FlowContext<I>> contexts) {
        log.warn("FlowEngine memo messenger does not support sending events.");
    }

    /**
     * 发送回调函数事件到引擎外部
     *
     * @param contexts 流程实例执行过程产生的contexts
     * @param <I> 流程实例执行时的入参数据类型，用于泛型推倒
     */
    @Override
    public <I> void sendCallback(List<FlowContext<I>> contexts) {
        log.warn("FlowEngine memo messenger does not support sending events.");
    }
}
