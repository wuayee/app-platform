/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.common.Constant;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.domain.enums.FlowNodeType;
import com.huawei.fit.waterflow.domain.enums.ParallelMode;
import com.huawei.fit.waterflow.domain.stream.nodes.JoinNode;
import com.huawei.fit.waterflow.domain.stream.nodes.ParallelNode;
import com.huawei.fit.waterflow.domain.stream.reactive.Processor;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 流程定义并行节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public class FlowParallelNode extends FlowNode {
    private static final Logger LOG = Logger.get(FlowParallelNode.class);

    private static final String PARALLEL_NODE_ID_SUFFIX = "parallel";

    private static final String JOIN_NODE_ID_SUFFIX = "join";

    private static final String PARALLEL_MODE = "mode";

    private final List<FlowNode> forks = new ArrayList<>();

    private Processor<FlowData, FlowData> joiner;

    /**
     * 获取节点内的processor
     * 一个parallel节点产生了两个processor，链式链接时入口是processor，出口是joiner
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
            this.processor = new ParallelNode<>(streamId, this.getMetaId(PARALLEL_NODE_ID_SUFFIX),
                    ParallelMode.parseFrom(properties.get(PARALLEL_MODE)), repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
            this.joiner = new JoinNode<>(streamId, this.getMetaId(JOIN_NODE_ID_SUFFIX), this::joinReducer, repo,
                    messenger, locks, FlowNodeType.JOIN);
            this.joiner.onError(errorHandler(streamId));
            this.forks.forEach(fork -> {
                Processor<FlowData, FlowData> forkProcessor = fork.getProcessor(streamId, repo, messenger, locks);
                this.processor.subscribe(forkProcessor);
                forkProcessor.subscribe(this.joiner);
            });
        }
        return this.processor;
    }

    @Override
    protected void subscribe(Publisher<FlowData> from, Subscriber<FlowData, FlowData> to, FlowEvent event) {
        this.joiner.subscribe(event.getMetaId(), to);
    }

    private void parallelJuster(FlowContext<FlowData> input) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "parallelJuster");
    }

    /**
     * 待处理 todo
     *
     * @param input 进入的context
     * @return 处理后的data
     */
    private FlowData joinReducer(FlowContext<FlowData> input) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "joinReducer");
    }

    private String getMetaId(String suffix) {
        return StringUtils.join(Constant.STREAM_ID_SEPARATOR, this.metaId, suffix);
    }
}
