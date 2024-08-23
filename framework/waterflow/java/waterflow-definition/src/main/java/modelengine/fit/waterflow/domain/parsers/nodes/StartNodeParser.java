/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.nodes;

import static modelengine.fit.waterflow.domain.enums.FlowNodeType.START;

import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowStartNode;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 开始节点解析类
 *
 * @author 杨祥宇
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
