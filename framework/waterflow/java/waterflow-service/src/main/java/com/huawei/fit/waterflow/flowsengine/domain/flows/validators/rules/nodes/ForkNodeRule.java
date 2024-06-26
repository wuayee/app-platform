/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;

/**
 * Fork节点校验规则
 *
 * @author g00564732
 * @since 2023/08/14
 */
public class ForkNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowNode 流程节点
     */
    @Override
    public void apply(FlowNode flowNode) {
        validateEmpty(flowNode.getEvents(), "fork node event size");
    }
}
