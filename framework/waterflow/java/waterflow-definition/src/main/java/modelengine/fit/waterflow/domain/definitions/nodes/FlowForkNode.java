/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes;

import static modelengine.fit.waterflow.common.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;

import lombok.Getter;
import modelengine.fit.waterflow.common.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.stream.nodes.Node;
import modelengine.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fitframework.log.Logger;

import java.util.Optional;

/**
 * 流程定义结束节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public class FlowForkNode extends FlowNode {
    private static final Logger LOG = Logger.get(FlowForkNode.class);

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
        if (!Optional.ofNullable(processor).isPresent()) {
            this.processor = new Node<>(streamId, this.metaId, this::forkJuster, repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
        }
        return this.processor;
    }

    private FlowData forkJuster(FlowContext<FlowData> input) { // fork节点是特殊的mapper，本质是juster，处理完input并将input返回
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "forkJuster");
    }
}
