/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.waterflow.common.Constant;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.nodes.JoinNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.nodes.ParallelNode;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 流程定义并行节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public class FlowParallelNode extends FlowNode {
    private static final Logger log = Logger.get(FlowParallelNode.class);

    private static final String PARALLEL_NODE_ID_SUFFIX = "parallel";

    private static final String JOIN_NODE_ID_SUFFIX = "join";

    private static final String PARALLEL_MODE = "mode";

    private final List<FlowNode> forks = new ArrayList<>();

    private FitStream.Processor<FlowData, FlowData> joiner;

    /**
     * 获取节点内的processor
     * 一个parallel节点产生了两个processor，链式链接时入口是processor，出口是joiner
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Processor}
     */
    @Override
    public FitStream.Processor<FlowData, FlowData> getProcessor(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        if (!Optional.ofNullable(this.processor).isPresent()) {
            this.processor = new ParallelNode<>(streamId, this.getMetaId(PARALLEL_NODE_ID_SUFFIX), this::parallelJuster,
                    ParallelMode.parseFrom(cast(properties.get(PARALLEL_MODE))), repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
            this.joiner = new JoinNode<>(streamId, this.getMetaId(JOIN_NODE_ID_SUFFIX), this::joinReducer, repo,
                    messenger, locks, FlowNodeType.JOIN);
            this.joiner.onError(errorHandler(streamId));
            this.forks.forEach(fork -> {
                FitStream.Processor<FlowData, FlowData> forkProcessor = fork.getProcessor(streamId, repo, messenger,
                        locks);
                this.processor.subscribe(forkProcessor);
                forkProcessor.subscribe(this.joiner);
            });
        }
        return this.processor;
    }

    @Override
    protected void subscribe(FitStream.Publisher<FlowData> from, FitStream.Subscriber<FlowData, FlowData> to,
            FlowEvent event) {
        this.joiner.subscribe(event.getMetaId(), to);
    }

    private void parallelJuster(FlowContext<FlowData> input) {
        throw new JobberException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "parallelJuster");
    }

    private FlowData joinReducer(List<FlowContext<FlowData>> inputs) {
        throw new JobberException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "joinReducer");
    }

    private String getMetaId(String suffix) {
        return StringUtils.join(Constant.STREAM_ID_SEPARATOR, this.metaId, suffix);
    }
}
