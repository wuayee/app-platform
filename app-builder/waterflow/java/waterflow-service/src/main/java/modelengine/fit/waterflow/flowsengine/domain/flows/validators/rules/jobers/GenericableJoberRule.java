/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fitframework.inspection.Validation;

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
