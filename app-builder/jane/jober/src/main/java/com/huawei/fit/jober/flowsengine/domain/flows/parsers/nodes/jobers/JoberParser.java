/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.jobers;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowDataConverterType;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * 自动任务解析接口
 *
 * @author y00679285
 * @since 2023/8/15
 */
public interface JoberParser {
    /**
     * 解析自动任务
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点任务对象
     */
    FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex);

    /**
     * commonParse
     *
     * @param flowJober flowJobber
     * @param flowGraphData flowGraphData
     * @param nodeIndex nodeIndex
     */
    default void commonParse(FlowJober flowJober, FlowGraphData flowGraphData, int nodeIndex) {
        flowJober.setNodeMetaId(flowGraphData.getNodeMetaId(nodeIndex));
        flowJober.setName(flowGraphData.getNodeJoberName(nodeIndex));
        flowJober.setFitables(new LinkedHashSet<>());
        flowJober.setExceptionFitables(flowGraphData.getFlowExceptionFitables());
        flowJober.setProperties(flowGraphData.getNodeJoberProperties(nodeIndex));
        flowJober.setFitablesConfig(flowGraphData.getNodeJoberFitableConfig(nodeIndex));
        Optional.ofNullable(flowGraphData.getNodeJoberConverter(nodeIndex))
                .ifPresent(config -> flowJober.setConverter(
                        FlowDataConverterType.getType((String) config.get("type")).getParser().parse(config)));
    }
}
