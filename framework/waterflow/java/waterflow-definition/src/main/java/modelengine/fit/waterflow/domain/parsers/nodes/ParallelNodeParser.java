/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.nodes;

import static modelengine.fit.waterflow.common.ErrorCodes.FLOW_ENGINE_PARSER_NOT_SUPPORT;

import modelengine.fit.waterflow.common.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

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
