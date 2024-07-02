/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.To;
import com.huawei.fitframework.log.Logger;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程定义结束节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author g00564732
 * @since 2023/08/14
 */
@Getter
public class FlowEndNode extends FlowNode {
    private static final Logger log = Logger.get(FlowEndNode.class);

    private FitStream.Subscriber<FlowData, FlowData> subscriber;

    @Override
    public FitStream.Subscriber<FlowData, FlowData> getSubscriber(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        if (!Optional.ofNullable(this.subscriber).isPresent()) {
            this.subscriber = new To<>(streamId, this.metaId, this::endProduce, repo, messenger, locks, this.type);
            setCallback(this.subscriber, messenger);
        }
        return this.subscriber;
    }

    private List<FlowData> endProduce(List<FlowContext<FlowData>> input) {
        return input.stream().map(i -> {
            Map<String, Object> contextData = i.getData().getContextData();
            contextData.put("nodeMetaId", getMetaId());
            contextData.put("nodeType", getType().getCode());
            return i.getData();
        }).collect(Collectors.toList());
    }
}
