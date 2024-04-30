/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.parsers.nodes.tasks;

import com.huawei.fit.waterflow.domain.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.domain.enums.FlowTaskType;
import com.huawei.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 手动任务解析接口
 *
 * @author y00679285
 * @since 1.0
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
        flowTask.setTaskType(FlowTaskType.getTaskType(flowGraphData.getNodeTaskType(nodeIndex)));
        flowTask.setExceptionFitables(flowGraphData.getFlowExceptionFitables());
        flowTask.setProperties(flowGraphData.getNodeTaskProperties(nodeIndex));
        return flowTask;
    }
}
