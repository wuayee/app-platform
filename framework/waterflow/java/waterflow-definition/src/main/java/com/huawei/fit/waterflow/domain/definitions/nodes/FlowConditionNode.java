/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes;

import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.domain.stream.nodes.ConditionsNode;
import com.huawei.fit.waterflow.domain.stream.operators.Operators;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fit.waterflow.domain.utils.OhScriptExecutor;
import modelengine.fitframework.log.Logger;

import lombok.Getter;

import java.util.Optional;

/**
 * 流程定义条件节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public class FlowConditionNode extends FlowNode {
    private static final Logger LOG = Logger.get(FlowConditionNode.class);

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
            this.processor = new ConditionsNode<>(streamId, this.metaId, repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
        }
        return this.processor;
    }

    @Override
    protected void subscribe(Publisher<FlowData> from, Subscriber<FlowData, FlowData> to, FlowEvent event) {
        from.subscribe(event.getMetaId(), to, this.getWhether(from.getStreamId(), event));
    }

    private Operators.Whether<FlowData> getWhether(String streamId, FlowEvent event) {
        LOG.info("[flowEngines] stream {} condition node {} with origin rule {}", streamId, this.metaId,
                event.getConditionRule());

        return (input) -> {
            String conditionRule = event.getConditionRule();
            LOG.info("[flowEngines] stream {} condition node {} with rule {}", streamId, this.metaId, conditionRule);
            return OhScriptExecutor.evaluateConditionRule(input, conditionRule);
        };
    }
}
