/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.manual.operation.operator;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;

import java.util.List;

/**
 * 任务处理接口
 *
 * @author 晏钰坤
 * @since 2023/9/15
 */
public interface Operator {
    /**
     * 处理流程中的手动任务
     *
     * @param contexts 流程流转数据
     * @param task 流程定义中的手动任务
     */
    void operate(List<FlowContext<FlowData>> contexts, FlowTask task);
}
