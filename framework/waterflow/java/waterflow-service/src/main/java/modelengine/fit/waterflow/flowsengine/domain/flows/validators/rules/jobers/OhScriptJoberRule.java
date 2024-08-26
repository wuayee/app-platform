/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties;
import modelengine.fitframework.inspection.Validation;

/**
 * OhScriptJoberRule
 *
 * @author 杨祥宇
 * @since 2023/10/31
 */
public class OhScriptJoberRule implements JoberRule {
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getProperties().get(FlowJoberProperties.ENTITY.getValue()),
                exception("flow ohScript jober entity"));
    }
}
