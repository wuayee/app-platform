/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowConditionNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 条件节点解析类
 *
 * @author 杨祥宇
 * @since 2023/8/15
 */
public class ConditionNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        FlowNode flowNode = new FlowConditionNode();
        flowNode.setType(FlowNodeType.CONDITION);
        commonParse(flowNode, flowGraphData, index);
        return flowNode;
    }
}
