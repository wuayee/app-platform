/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.nodes;

import static modelengine.fit.waterflow.domain.enums.FlowNodeType.CONDITION;

import modelengine.fit.waterflow.domain.definitions.nodes.FlowConditionNode;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 条件节点解析类
 *
 * @author 杨祥宇
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
