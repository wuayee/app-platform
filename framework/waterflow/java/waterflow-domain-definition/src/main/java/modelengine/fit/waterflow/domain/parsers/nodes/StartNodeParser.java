/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

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
