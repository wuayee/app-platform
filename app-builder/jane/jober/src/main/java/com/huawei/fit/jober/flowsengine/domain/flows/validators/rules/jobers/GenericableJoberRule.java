/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.validators.rules.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberType.GENERICABLE_JOBER;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fitframework.inspection.Validation;

/**
 * genericable任务调用的校验规则
 *
 * @author s00558940
 * @since 2024/4/22
 */
public class GenericableJoberRule implements JoberRule {
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flowJoberType"));
        Validation.equals(GENERICABLE_JOBER, flowJober.getType(), exception("flowJoberType"));
        Validation.equals(1, flowJober.getFitables().size(), exception("flowJoberFitables"));
    }
}
