/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.jobers;

import static com.huawei.fit.waterflow.domain.enums.FlowJoberType.GENERAL_JOBER;

import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowGeneralJober;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * General任务解析接口
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class GeneralJoberParser implements JoberParser {
    /**
     * 解析自动任务
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点任务对象
     */
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowJober flowJober = new FlowGeneralJober();
        flowJober.setType(GENERAL_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        return flowJober;
    }
}
