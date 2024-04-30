/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.jobers;

import static com.huawei.fit.waterflow.domain.enums.FlowJoberType.OHSCRIPT_JOBER;

import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowOhScriptJober;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * ohScript脚本任务解析器
 *
 * @author y00679285
 * @since 1.0
 */
public class OhScriptJoberParser implements JoberParser {
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowJober flowJober = new FlowOhScriptJober();
        flowJober.setType(OHSCRIPT_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        return flowJober;
    }
}
