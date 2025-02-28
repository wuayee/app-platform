/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.parsers.nodes;

import static modelengine.fit.waterflow.domain.enums.FlowNodeType.STATE;

import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowStateNode;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 普通节点解析类
 *
 * @author 杨祥宇
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
