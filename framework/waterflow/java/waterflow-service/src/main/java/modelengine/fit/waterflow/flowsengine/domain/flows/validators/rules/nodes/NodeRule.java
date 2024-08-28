/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeTriggerMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.Rules;

/**
 * 不同流程节点类型校验规则
 *
 * @author 高诗意
 * @since 2023/08/29
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
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
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
        if (flowNode.getTriggerMode() != FlowNodeTriggerMode.AUTO) {
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, message);
        }
    }
}
