/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.parsers.nodes;

import modelengine.fit.jade.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

import static modelengine.fit.jade.waterflow.ErrorCodes.FLOW_ENGINE_PARSER_NOT_SUPPORT;

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
