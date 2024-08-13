/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fitframework.log.Logger;

import lombok.Getter;

import java.util.Optional;

/**
 * 流程定义开始节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public class FlowStartNode extends FlowNode {
    private static final Logger log = Logger.get(FlowStartNode.class);

    private FitStream.Publisher<FlowData> publisher;

    @Override
    public FitStream.Publisher<FlowData> getPublisher(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        if (!Optional.ofNullable(this.publisher).isPresent()) {
            this.publisher = new From<>(streamId, this.metaId, repo, messenger, locks);
        }
        return this.publisher;
    }
}
