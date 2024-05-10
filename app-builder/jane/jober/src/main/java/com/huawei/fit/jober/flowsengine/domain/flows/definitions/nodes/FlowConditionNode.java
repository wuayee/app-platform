/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Processor;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Publisher;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.FitStream.Subscriber;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.Processors.Whether;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.ConditionsNode;
import com.huawei.fit.jober.flowsengine.utils.OhScriptExecutor;
import com.huawei.fitframework.log.Logger;

import com.google.common.collect.Lists;

import lombok.Getter;

import java.util.Optional;

/**
 * 流程定义条件节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author g00564732
 * @since 2023/08/14
 */
@Getter
public class FlowConditionNode extends FlowNode {
    private static final Logger log = Logger.get(FlowConditionNode.class);

    /**
     * 获取节点内的processor
     * 只有普通节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link Processor}
     */
    @Override
    public Processor<FlowData, FlowData> getProcessor(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        if (!Optional.ofNullable(this.processor).isPresent()) {
            this.processor = new ConditionsNode<>(streamId, this.metaId, this::conditionalJuster, repo, messenger,
                    locks, this.type);
            this.processor.onError(errorHandler(streamId));
        }
        return this.processor;
    }

    @Override
    protected void subscribe(Publisher<FlowData> from, Subscriber<FlowData, FlowData> to, FlowEvent event) {
        from.subscribe(event.getMetaId(), to, null, this.getWhether(from.getStreamId(), event));
    }

    private Whether<FlowData> getWhether(String streamId, FlowEvent event) {
        log.info("[flowEngines] stream {} condition node {} with origin rule {}", streamId, this.metaId,
                event.getConditionRule());

        return (input) -> {
            String conditionRule = event.getConditionRule();
            log.info("[flowEngines] stream {} condition node {} with rule {}", streamId, this.metaId, conditionRule);
            return OhScriptExecutor.evaluateConditionRule(input.getData(), conditionRule);
        };
    }

    private void conditionalJuster(FlowContext<FlowData> input) {
        Optional.ofNullable(this.jober).ifPresent(flowJober -> flowJober.execute(Lists.newArrayList(input.getData())));
    }
}
