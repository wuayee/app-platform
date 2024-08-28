/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.enums;

import static modelengine.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.domain.parsers.nodes.ConditionNodeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.EndNodeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.NodeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.ParallelNodeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.StartNodeParser;
import modelengine.fit.waterflow.domain.parsers.nodes.StateNodeParser;
import modelengine.fit.waterflow.domain.validators.rules.nodes.ConditionNodeRule;
import modelengine.fit.waterflow.domain.validators.rules.nodes.EndNodeRule;
import modelengine.fit.waterflow.domain.validators.rules.nodes.ForkNodeRule;
import modelengine.fit.waterflow.domain.validators.rules.nodes.NodeRule;
import modelengine.fit.waterflow.domain.validators.rules.nodes.ParallelNodeRule;
import modelengine.fit.waterflow.domain.validators.rules.nodes.StartNodeRule;
import modelengine.fit.waterflow.domain.validators.rules.nodes.StateNodeRule;

import java.util.Arrays;

/**
 * 流程定义节点类型
 *
 * @author g00564732
 * @since 1.0
 */
@Getter
public enum FlowNodeTypeParser {
    START(FlowNodeType.START, false, new StartNodeParser(), new StartNodeRule()),
    STATE(FlowNodeType.STATE, false, new StateNodeParser(), new StateNodeRule()),
    CONDITION(FlowNodeType.CONDITION, false, new ConditionNodeParser(), new ConditionNodeRule()),
    PARALLEL(FlowNodeType.PARALLEL, false, new ParallelNodeParser(), new ParallelNodeRule()),
    FORK(FlowNodeType.FORK, true, null, new ForkNodeRule()),
    JOIN(FlowNodeType.JOIN, true, null, null),
    EVENT(FlowNodeType.EVENT, true, null, null),
    END(FlowNodeType.END, false, new EndNodeParser(), new EndNodeRule());

    private final FlowNodeType flowNodeType;

    private final boolean subNode;

    private final NodeParser nodeParser;

    private final NodeRule nodeRule;

    FlowNodeTypeParser(FlowNodeType flowNodeType, boolean subNode, NodeParser nodeParser, NodeRule nodeRule) {
        this.flowNodeType = flowNodeType;
        this.subNode = subNode;
        this.nodeParser = nodeParser;
        this.nodeRule = nodeRule;
    }

    /**
     * getNodeType
     *
     * @param flowNodeType 节点类型
     * @return FlowNodeTypeParser
     */
    public static FlowNodeTypeParser getType(FlowNodeType flowNodeType) {
        return Arrays.stream(values())
                .filter(value -> flowNodeType.equals(value.getFlowNodeType()))
                .findFirst()
                .orElseThrow(
                        () -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowNodeType", flowNodeType.getCode()));
    }
}
