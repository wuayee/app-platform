/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.nodes.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberType.HTTP_JOBER;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowHttpJober;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowGraphData;

/**
 * HttpJoberParser
 *
 * @author 00693950
 * @since 2023/10/18
 */
public class HttpJoberParser implements JoberParser {
    /**
     * 解析自动任务
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点任务对象
     */
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowJober flowJober = new FlowHttpJober();
        flowJober.setType(HTTP_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        return flowJober;
    }
}
