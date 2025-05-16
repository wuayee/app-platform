/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.callbacks.PreSendCallbackInfo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 条件节点，也是match的起始节点
 * parallel节点覆盖了from的subscribe方法，允许subscribe到多个subscriber
 * parallel只允许map处理器，避免复杂设计
 * 辉子 2019-12-17
 *
 * @param <I>传入数据类型
 * @author 高诗意
 * @since 2023/08/14
 */
public class ConditionsNode<I> extends Node<I, I> {
    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public ConditionsNode(String streamId, Processors.Just<FlowContext<I>> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, i -> {
            processor.process(i);
            return i.getData();
        }, repo, messenger, locks, () -> initFrom(streamId, repo, messenger, locks));
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public ConditionsNode(String streamId, String nodeId, Processors.Just<FlowContext<I>> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, repo, messenger, locks);
        this.id = nodeId;
        this.nodeType = nodeType;
    }

    /**
     * 初始化from节点，并根据订阅者的匹配规则，将匹配的上下文缓存到订阅者中
     * 只publish给符合条件的subscription
     *
     * @param <I> 传入数据类型
     * @param streamId stream流程ID
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @return 返回初始化后的from节点
     */
    private static <I> From<I> initFrom(String streamId, FlowContextRepo repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        return new From<I>(streamId, repo, messenger, locks) {
            @Override
            public void offer(List<FlowContext<I>> contexts, Consumer<PreSendCallbackInfo<I>> preSendCallback) {
                Map<FitStream.Subscription<I, ?>, List<FlowContext<I>>> matchedContexts = new LinkedHashMap<>();
                this.getSubscriptions().forEach(w -> {
                    List<FlowContext<I>> matched = contexts.stream()
                            .filter(c -> w.getWhether().is(c))
                            .peek(c -> c.setNextPositionId(w.getId()))
                            .collect(Collectors.toList());
                    matched.forEach(contexts::remove);
                    matchedContexts.put(w, matched);
                });
                PreSendCallbackInfo<I> callbackInfo = new PreSendCallbackInfo<>(matchedContexts, contexts);
                preSendCallback.accept(callbackInfo);
                matchedContexts.forEach(FitStream.Subscription::cache);
            }
        };
    }
}
