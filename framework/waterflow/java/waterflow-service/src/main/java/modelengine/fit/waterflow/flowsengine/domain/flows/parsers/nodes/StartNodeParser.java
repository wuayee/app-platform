/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowStartNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 开始节点解析类
 *
 * @author 杨祥宇
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
