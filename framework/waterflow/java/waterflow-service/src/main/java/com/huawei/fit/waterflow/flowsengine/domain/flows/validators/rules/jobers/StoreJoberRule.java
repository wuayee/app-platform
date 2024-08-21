/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowStoreJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fitframework.inspection.Validation;

/**
 * store jober的规则
 *
 * @author 宋永坦
 * @since 2024/5/8
 */
public class StoreJoberRule implements JoberRule {
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flowJoberType"));
        Validation.equals(FlowJoberType.STORE_JOBER, flowJober.getType(), exception("flowJoberType"));
        Validation.isTrue(flowJober instanceof FlowStoreJober, exception("flowJoberType"));
        FlowStoreJober flowStoreJober = cast(flowJober);
        Validation.notBlank(flowStoreJober.getServiceMeta().getUniqueName(), exception("uniqueName"));
        Validation.notNull(flowStoreJober.getServiceMeta().getParams(), exception("params"));
    }
}
