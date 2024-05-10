/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.validators.rules;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;

import java.util.regex.Pattern;

/**
 * 节点事件校验规则
 *
 * @author g00564732
 * @since 2023/08/14
 */
@Component
public class EventsRule implements FlowRule {
    private static final int META_ID_SIZE = 6;

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9 ]");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link JobberParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet()
                .forEach(nodeId -> flowDefinition.getFlowNode(nodeId)
                        .getEvents()
                        .forEach(flowEvent -> this.apply(flowDefinition, flowEvent)));
    }

    private void apply(FlowDefinition flowDefinition, FlowEvent flowEvent) {
        validateMetaId(flowEvent.getMetaId());
        Validation.notBlank(flowEvent.getFrom(), exception("flow event from metaId empty"));
        Validation.notNull(flowDefinition.getFlowNode(flowEvent.getFrom()),
                exception("flow event from metaId invalid"));
        Validation.notBlank(flowEvent.getTo(), exception("flow event to metaId empty"));
        Validation.notNull(flowDefinition.getFlowNode(flowEvent.getTo()), exception("flow event to metaId invalid"));
        Validation.isFalse(flowEvent.getFrom().equals(flowEvent.getTo()), exception("flow event from equal to metaId"));
        applyConditionRule(flowDefinition, flowEvent);
    }

    private void applyConditionRule(FlowDefinition flowDefinition, FlowEvent flowEvent) {
        FlowNode flowNode = flowDefinition.getFlowNode(flowEvent.getFrom());
        if (!flowNode.belongTo(FlowNodeType.CONDITION)) {
            validateBlank(flowEvent.getConditionRule(), "normal node condition rule");
            return;
        }
        Validation.notBlank(flowEvent.getConditionRule(), exception("condition node condition rule"));
    }

    private void validateMetaId(String metaId) {
        Validation.notBlank(metaId, exception("node event metaId"));
        Validation.same(metaId.length(), META_ID_SIZE, exception("node event metaId"));
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(metaId).find(), exception("node event metaId"));
    }
}
