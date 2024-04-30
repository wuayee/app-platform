/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.jobers;

import static com.huawei.fit.waterflow.domain.enums.FlowJoberType.ECHO_JOBER;

import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowEchoJober;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * Echo任务解析接口
 *
 * @author y00679285
 * @since 1.0
 */
public class EchoJoberParser implements JoberParser {
    /**
     * 解析自动任务
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点任务对象
     */
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowJober flowJober = new FlowEchoJober();
        flowJober.setType(ECHO_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        return flowJober;
    }
}
