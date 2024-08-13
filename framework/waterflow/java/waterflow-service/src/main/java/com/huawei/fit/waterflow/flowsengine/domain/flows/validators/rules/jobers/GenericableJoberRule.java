/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import com.huawei.fitframework.inspection.Validation;

/**
 * genericable任务调用的校验规则
 *
 * @author 宋永坦
 * @since 2024/4/22
 */
public class GenericableJoberRule implements JoberRule {
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flowJoberType"));
        Validation.equals(FlowJoberType.GENERICABLE_JOBER, flowJober.getType(), exception("flowJoberType"));
        Validation.equals(1, flowJober.getFitables().size(), exception("flowJoberFitables"));
    }
}
