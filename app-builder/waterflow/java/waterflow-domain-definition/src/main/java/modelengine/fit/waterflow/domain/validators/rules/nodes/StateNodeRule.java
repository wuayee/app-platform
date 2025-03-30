/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.validators.rules.nodes;

import modelengine.fit.jade.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.definitions.nodes.FlowNode;
import modelengine.fitframework.inspection.Validation;

/**
 * 普通节点校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
public class StateNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowNode 流程节点
     */
    @Override
    public void apply(FlowNode flowNode) {
        Validation.same(flowNode.getEvents().size(), EXPECT_EVENT_SIZE, exception("state node event size"));
        if (!flowNode.getTriggerMode().isAuto()) {
            Validation.notNull(flowNode.getTask(), exception("Flow node task error"));
        } else {
            Validation.notNull(flowNode.getJober(), exception("Flow node jober should not be null"));
        }
    }
}
