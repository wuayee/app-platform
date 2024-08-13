/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.FlowDefinition;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 节点手动任务校验规则
 *
 * @author 高诗意
 * @since 1.0
 */
@Component
public class JobersRule implements FlowRule {
    private static final int MAX_NAME_SIZE = 256;

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*<?{}|]");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet()
                .forEach(nodeId -> Optional.ofNullable(flowDefinition.getFlowNode(nodeId).getJober())
                        .ifPresent(this::apply));
    }

    private void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flow jober type"));
        Validation.notNull(flowJober.getFitables(), exception("flow jober fitables"));
        validateName(flowJober.getName());
        Optional.ofNullable(flowJober.getType().getJoberRule()).ifPresent(joberRule -> joberRule.apply(flowJober));
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(name).find(),
                exception("flow jober name, name contains special characters"));
        Validation.lessThan(name.length(), MAX_NAME_SIZE, exception("flow jober name, name length over 256"));
    }
}
