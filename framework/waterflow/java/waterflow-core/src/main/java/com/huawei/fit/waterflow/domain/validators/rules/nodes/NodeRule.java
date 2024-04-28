/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.nodes;

import static com.huawei.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeTriggerMode.AUTO;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.validators.rules.Rules;

/**
 * 不同流程节点类型校验规则
 *
 * @author g00564732
 * @since 1.0
 */
public interface NodeRule extends Rules {
    /**
     * MINIMUM_EVENT_SIZE
     */
    int MINIMUM_EVENT_SIZE = 2;
    /**
     * EXPECT_EVENT_SIZE
     */
    int EXPECT_EVENT_SIZE = 1;

    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowNode 流程节点
     */
    void apply(FlowNode flowNode);

    /**
     * validateTriggerMode
     *
     * @param flowNode flowNode
     * @param message message
     */
    default void validateTriggerMode(FlowNode flowNode, String message) {
        if (flowNode.getTriggerMode() != AUTO) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, message);
        }
    }
}
