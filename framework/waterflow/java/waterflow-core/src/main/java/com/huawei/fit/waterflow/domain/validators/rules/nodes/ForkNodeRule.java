/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.nodes;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;

/**
 * Fork节点校验规则
 *
 * @author g00564732
 * @since 1.0
 */
public class ForkNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowNode 流程节点
     */
    @Override
    public void apply(FlowNode flowNode) {
        validateEmpty(flowNode.getEvents(), "fork node event size");
    }
}
