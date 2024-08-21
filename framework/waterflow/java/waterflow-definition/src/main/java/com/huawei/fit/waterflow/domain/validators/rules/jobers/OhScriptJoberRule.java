/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators.rules.jobers;

import static com.huawei.fit.waterflow.domain.enums.FlowJoberProperties.ENTITY;

import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import modelengine.fitframework.inspection.Validation;

/**
 * ohScript脚本规则
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class OhScriptJoberRule implements JoberRule {
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getProperties().get(ENTITY.getValue()), exception("flow ohScript jober entity"));
    }
}
