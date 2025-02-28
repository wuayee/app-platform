/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.parsers.nodes.tasks;

import modelengine.fit.waterflow.domain.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.domain.enums.FlowTaskType;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * 手动任务解析接口
 *
 * @author 杨祥宇
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
