/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes;

import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.STATE;

import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowStateNode;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 普通节点解析类
 *
 * @author y00679285
 * @since 1.0
 */
public class StateNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        FlowNode flowNode = new FlowStateNode();
        flowNode.setType(STATE);
        commonParse(flowNode, flowGraphData, index);
        return flowNode;
    }
}
