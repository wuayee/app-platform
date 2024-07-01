/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;

/**
 * 借宿节点校验规则
 *
 * @author g00564732
 * @since 2023/08/14
 */
public class EndNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
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
