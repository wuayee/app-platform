/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.parsers.nodes.jobers;

import modelengine.fit.waterflow.domain.definitions.nodes.jobers.FlowHttpJober;
import modelengine.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.domain.enums.FlowJoberType;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * http调用类型任务解析器
 *
 * @author 晏钰坤
 * @since 1.0
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
        flowJober.setType(FlowJoberType.HTTP_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        return flowJober;
    }
}
