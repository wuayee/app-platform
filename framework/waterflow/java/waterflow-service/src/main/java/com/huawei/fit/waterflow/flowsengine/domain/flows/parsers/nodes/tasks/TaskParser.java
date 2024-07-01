/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.tasks;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowDataConverterType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowGraphData;

import java.util.Optional;

/**
 * 手动任务解析接口
 *
 * @author y00679285
 * @since 2023/9/20
 */
public class TaskParser {
    /**
     * 按照手动任务规则解析手动任务
     *
     * @param flowGraphData {@link FlowGraphData} 流程json操作封装对象
     * @param nodeIndex 当前节点索引
     * @return 流程节点手动任务对象
     */
    public FlowTask parseTask(FlowGraphData flowGraphData, int nodeIndex) {
        FlowTask flowTask = new FlowTask();
        flowTask.setTaskId(flowGraphData.getNodeTaskId(nodeIndex));
        flowTask.setTaskType(flowGraphData.getNodeTaskType(nodeIndex).map(FlowTaskType::getTaskType).orElse(null));
        flowTask.setExceptionFitables(flowGraphData.getFlowExceptionFitables());
        flowTask.setProperties(flowGraphData.getNodeTaskProperties(nodeIndex));
        Optional.ofNullable(flowGraphData.getNodeTaskConverter(nodeIndex))
                .ifPresent(config -> flowTask.setConverter(
                        FlowDataConverterType.getType((String) config.get("type")).getParser().parse(config)));
        return flowTask;
    }
}
