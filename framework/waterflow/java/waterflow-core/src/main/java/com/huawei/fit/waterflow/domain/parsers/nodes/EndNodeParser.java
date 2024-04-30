/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes;

import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.END;

import com.huawei.fit.waterflow.domain.definitions.nodes.FlowEndNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 结束节点解析类
 *
 * @author y00679285
 * @since 1.0
 */
public class EndNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        FlowNode flowNode = new FlowEndNode();
        flowNode.setType(END);
        commonParse(flowNode, flowGraphData, index);
        return flowNode;
    }
}
