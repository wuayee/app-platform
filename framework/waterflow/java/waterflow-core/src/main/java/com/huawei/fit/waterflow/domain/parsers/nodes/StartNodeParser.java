/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes;

import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.START;

import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowStartNode;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 开始节点解析类
 *
 * @author y00679285
 * @since 1.0
 */
public class StartNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        FlowNode flowNode = new FlowStartNode();
        flowNode.setType(START);
        commonParse(flowNode, flowGraphData, index);
        return flowNode;
    }
}
