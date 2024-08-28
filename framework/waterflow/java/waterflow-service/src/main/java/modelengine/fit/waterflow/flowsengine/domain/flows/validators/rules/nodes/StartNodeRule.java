/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fitframework.inspection.Validation;

/**
 * 开始节点校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class StartNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowNode 流程节点
     */
    @Override
    public void apply(FlowNode flowNode) {
        Validation.same(flowNode.getEvents().size(), EXPECT_EVENT_SIZE, exception("start node event size"));
        validateNull(flowNode.getJober(), "start node jober should be null");
        validateTriggerMode(flowNode, "start node trigger mode");
    }
}
