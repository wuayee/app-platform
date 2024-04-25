/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.nodes;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fitframework.inspection.Validation;

/**
 * 开始节点校验规则
 *
 * @author g00564732
 * @since 1.0
 */
public class StartNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowNode 流程节点
     */
    public void apply(FlowNode flowNode) {
        Validation.same(flowNode.getEvents().size(), EXPECT_EVENT_SIZE, exception("start node event size"));
        validateNull(flowNode.getJober(), "start node jober should be null");
        validateTriggerMode(flowNode, "start node trigger mode");
    }
}
