/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules;

import static com.huawei.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.FlowDefinition;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.enums.FlowNodeType;
import com.huawei.fit.waterflow.domain.enums.FlowNodeTypeParser;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流程节点校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
@Component
public class NodesRule implements FlowRule {
    private static final int META_ID_SIZE = 6;

    private static final String SPECIAL_CHAR_REG = "[^a-zA-Z0-9 ]";

    private static final Pattern BRACES_PATTERN = Pattern.compile("^\\{\\{(.+?)\\}\\}$");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet().stream().map(flowDefinition::getFlowNode).forEach(this::apply);
    }

    private void apply(FlowNode flowNode) {
        validateMetaId(flowNode.getMetaId());
        validateType(flowNode.getType());
        validateTriggerMode(flowNode);
        Validation.notBlank(flowNode.getName(), exception("flow node name, name can not be blank"));
        Optional.ofNullable(flowNode.getJoberFilter())
                .ifPresent(flowFilter -> Optional.ofNullable(flowFilter.getFilterType().getFilterRule())
                        .ifPresent((filterRule) -> filterRule.apply(flowFilter)));
        Optional.ofNullable(flowNode.getTaskFilter())
                .ifPresent(flowFilter -> Optional.ofNullable(flowFilter.getFilterType().getFilterRule())
                        .ifPresent((filterRule) -> filterRule.apply(flowFilter)));
        Optional.ofNullable(flowNode.getType()).map(FlowNodeTypeParser::getType).map(FlowNodeTypeParser::getNodeRule)
                .ifPresent(nodeRule -> nodeRule.apply(flowNode));
        Optional.ofNullable(flowNode.getProperties().get("flowContext")).ifPresent(this::checkFlowContext);
    }

    private void checkFlowContext(String flowContext) {
        Matcher matcher = BRACES_PATTERN.matcher(flowContext);
        if (!matcher.find() || Objects.equals(matcher.group(1), "")) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID,
                    "flowContext has been config, but the output key is empty");
        }
    }

    private void validateType(FlowNodeType type) {
        Validation.notNull(type, exception("flow node type, node type can not be null"));
    }

    private void validateMetaId(String metaId) {
        Validation.notBlank(metaId, exception("flow node metaId, metaId can not be blank"));
        Validation.same(metaId.length(), META_ID_SIZE, exception("flow node metaId size must be 6"));
        Pattern pattern = Pattern.compile(SPECIAL_CHAR_REG);
        Validation.isFalse(pattern.matcher(metaId).find(), exception("flow node metaId not allow special char"));
    }

    private void validateTriggerMode(FlowNode flowNode) {
        Validation.notNull(flowNode.getTriggerMode(), exception("flow node trigger mode, can not be null"));
    }
}
