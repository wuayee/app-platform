/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_ENGINE_PARSER_NOT_SUPPORT;

import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 平行节点解析类
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class ParallelNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        throw new WaterflowException(FLOW_ENGINE_PARSER_NOT_SUPPORT, "ParallelNodeParser::parseNode");
    }
}
