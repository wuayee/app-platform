/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.validators.rules.jobers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowJoberProperties.ENTITY;

import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fitframework.inspection.Validation;

/**
 * OhScriptJoberRule
 *
 * @author y00679285
 * @since 2023/10/31
 */
public class OhScriptJoberRule implements JoberRule {
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getProperties().get(ENTITY.getValue()), exception("flow ohScript jober entity"));
    }
}
