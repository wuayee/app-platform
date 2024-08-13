/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.nodes;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;

/**
 * 借宿节点校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
public class EndNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowNode 流程节点
     */
    @Override
    public void apply(FlowNode flowNode) {
        validateEmpty(flowNode.getEvents(), "end node event size");
        validateNull(flowNode.getJober(), "end node jober can not be null");
        validateTriggerMode(flowNode, "end node trigger mode");
    }
}
