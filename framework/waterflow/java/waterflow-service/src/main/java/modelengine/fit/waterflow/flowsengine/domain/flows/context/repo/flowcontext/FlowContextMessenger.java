/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Subscriber;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;

/**
 * 异步化节点间操作
 * 辉子 2020-05-15
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public interface FlowContextMessenger {
    /**
     * 通知subscriber有新的数据到达
     * 数据会堆积在subscription节点
     * subscriber自行按照自己的压力要求request相应数量的数据
     *
     * @param <I> 流程实例执行时的入参数据类型，用于泛型推倒
     * @param type 发送节点处理事件类型，PRE_PROCESS类型为发送人工任务通知，PROCESS类型为节点本身的任务处理
     * @param subscriber 订阅者
     * @param context 流程实例执行过程产生的context
     */
    default <I> void send(ProcessType type, Subscriber<I, ?> subscriber, List<FlowContext<I>> context) {
        if (CollectionUtils.isEmpty(context)) {
            return;
        }
        subscriber.accept(type, context);
    }

    /**
     * 发送事件到引擎外部
     *
     * @param nodeId 节点ID
     * @param contexts 流程实例执行过程产生的contexts
     * @param <I> 流程实例执行时的入参数据类型，用于泛型推倒
     */
    <I> void send(String nodeId, List<FlowContext<I>> contexts);

    /**
     * 发送回调函数事件到引擎外部
     *
     * @param callback 回调函数.
     * @param contexts 流程实例执行过程产生的contexts
     * @param <I> 流程实例执行时的入参数据类型，用于泛型推倒
     */
    <I> void sendCallback(FlowCallback callback, List<FlowContext<I>> contexts);
}
