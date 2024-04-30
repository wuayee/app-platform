/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes;

import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.CONDITION;

import com.huawei.fit.waterflow.domain.definitions.nodes.FlowConditionNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 条件节点解析类
 *
 * @author y00679285
 * @since 1.0
 */
public class ConditionNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        FlowNode flowNode = new FlowConditionNode();
        flowNode.setType(CONDITION);
        commonParse(flowNode, flowGraphData, index);
        return flowNode;
    }
}
