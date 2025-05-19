/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.definitions.nodes;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import modelengine.fit.waterflow.domain.common.Constant;
import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.domain.stream.nodes.Blocks;
import modelengine.fit.waterflow.domain.stream.nodes.Node;
import modelengine.fit.waterflow.domain.stream.reactive.Processor;
import modelengine.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程定义状态节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public class FlowStateNode extends FlowNode {
    private static final Logger LOG = Logger.get(FlowStateNode.class);

    private static final String VARIABLE_REGEX = "\\{\\{(.+?)}}";

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
            this.processor = new Node<>(streamId, this.metaId, this::stateProduce, repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
            if (!triggerMode.isAuto()) {
                this.processor.block(new Blocks.FilterBlock<>());
                Optional.ofNullable(this.taskFilter).ifPresent(filter -> this.processor.preFilter(filter.filter()));
            }
            Optional.ofNullable(this.joberFilter).ifPresent(filter -> this.processor.postFilter(filter.filter()));
            setCallback(this.processor, messenger);
        }
        return this.processor;
    }

    private List<FlowData> stateProduce(List<FlowContext<FlowData>> inputs) {
        if (!Optional.ofNullable(this.jober).isPresent()) {
            return setFlowMetaToContextData(
                    inputs.stream().map(context -> context.getData()).collect(Collectors.toList()));
        }
        return setFlowMetaToContextData(
                this.jober.execute(inputs.stream().map(context -> context.getData()).collect(Collectors.toList())));
    }

    private List<FlowData> setFlowMetaToContextData(List<FlowData> inputs) {
        String output = properties.get("flowContext");

        Optional.ofNullable(output).ifPresent(flowContextStr -> {
            String outputKey = output.replaceAll(VARIABLE_REGEX, "$1");
            inputs.forEach(input -> {
                Map<String, String> returnMeta = new HashMap<>();
                returnMeta.put(Constant.OPERATOR_KEY, input.getOperator());
                String metaString = JSON.toJSONString(returnMeta);
                input.getContextData().put(outputKey, metaString);
            });
        });

        return inputs;
    }
}
