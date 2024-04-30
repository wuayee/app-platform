/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes;

import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.stream.nodes.To;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fitframework.log.Logger;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程定义结束节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author g00564732
 * @since 1.0
 */
@Getter
public class FlowEndNode extends FlowNode {
    private static final Logger LOG = Logger.get(FlowEndNode.class);

    private Subscriber<FlowData, FlowData> subscriber;

    @Override
    public Subscriber<FlowData, FlowData> getSubscriber(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        if (!Optional.ofNullable(this.subscriber).isPresent()) {
            this.subscriber = new To<>(streamId, this.metaId, this::endProduce, repo, messenger, locks, this.type);
            setCallback(this.subscriber, messenger);
        }
        return this.subscriber;
    }

    private List<FlowData> endProduce(List<FlowContext<FlowData>> input) {
        return input.stream().map(FlowContext::getData).collect(Collectors.toList());
    }
}
