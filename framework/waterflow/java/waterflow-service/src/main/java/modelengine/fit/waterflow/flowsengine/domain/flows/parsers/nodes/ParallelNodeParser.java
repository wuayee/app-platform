/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_ENGINE_PARSER_NOT_SUPPORT;

import com.huawei.fit.jober.common.exceptions.JobberException;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * 平行节点解析类
 *
 * @author 杨祥宇
 * @since 2023/8/16
 */
public class ParallelNodeParser implements NodeParser {
    @Override
    public FlowNode parseNode(FlowGraphData flowGraphData, int index) {
        throw new JobberException(FLOW_ENGINE_PARSER_NOT_SUPPORT, "ParallelNodeParser::parseNode");
    }
}
