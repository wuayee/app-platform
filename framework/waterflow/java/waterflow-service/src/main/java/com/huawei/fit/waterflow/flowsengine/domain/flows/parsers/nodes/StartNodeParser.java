/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowStartNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 开始节点解析类
 *
 * @author y00679285
 * @since 2023/8/15
 */
public class StartNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        FlowNode flowNode = new FlowStartNode();
        flowNode.setType(FlowNodeType.START);
        commonParse(flowNode, flowGraphData, index);
        return flowNode;
    }
}
