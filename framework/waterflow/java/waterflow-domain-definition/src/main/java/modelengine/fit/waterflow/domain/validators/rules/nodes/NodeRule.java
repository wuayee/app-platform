/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.validators.rules.nodes;

import static modelengine.fit.jade.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.waterflow.domain.enums.FlowNodeTriggerMode.AUTO;

import modelengine.fit.jade.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.domain.validators.rules.Rules;

/**
 * 不同流程节点类型校验规则
 *
 * @author 高诗意
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
