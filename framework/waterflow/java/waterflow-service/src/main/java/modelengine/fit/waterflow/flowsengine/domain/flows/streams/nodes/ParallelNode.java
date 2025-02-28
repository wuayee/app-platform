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
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.callbacks.PreSendCallbackInfo;

import java.util.List;
import java.util.function.Consumer;

/**
 * 平行节点，也是fork的起始节点
 * 有两个模式：either只要一个fork就算完成；all所有fork完成才完成
 * parallel节点覆盖了from的subscribe方法，允许subscribe到多个subscriber
 * parallel只允许just处理器，并且之后的节点在出现join前都只允许just处理器，否则join不了
 * 辉子 2019-12-17
 *
 * @param <I>传入数据类型
 * @author 高诗意
 * @since 2023/08/14
 */
public class ParallelNode<I> extends Node<I, I> {
    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param mode 并行节点执行模式
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public ParallelNode(String streamId, Processors.Just<FlowContext<I>> processor, ParallelMode mode, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, i -> {
            processor.process(i);
            return i.getData();
        }, repo, messenger, locks, () -> initFrom(streamId, mode, repo, messenger, locks));
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param mode 并行节点的模式
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public ParallelNode(String streamId, String nodeId, Processors.Just<FlowContext<I>> processor, ParallelMode mode,
            FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, processor, mode, repo, messenger, locks);
        this.id = nodeId;
        this.nodeType = nodeType;
    }

    /**
     * 初始化并行节点的from，并行节点的from会对传入的contexts进行处理，
     * 将每个context的id设置为其并行id，并将并行模式设置为mode
     *
     * @param <I> 传入数据类型
     * @param streamId stream流程ID
     * @param mode 并行节点的模式
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @return 返回一个经过处理的from
     */
    private static <I> From<I> initFrom(String streamId, ParallelMode mode, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        return new From<I>(streamId, repo, messenger, locks) {
            @Override
            public void offer(List<FlowContext<I>> contexts, Consumer<PreSendCallbackInfo<I>> preSendCallback) {
                contexts.forEach(c -> c.setParallel(c.getId()).setParallelMode(mode.name()));
                super.offer(contexts, preSendCallback);
            }
        };
    }
}
