/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers;

import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fitframework.log.Logger;

import java.util.List;
import java.util.Map;

/**
 * http的jober实现
 *
 * @author 00693950
 * @since 2023/10/18
 */
public class FlowHttpJober extends FlowJober {
    private static final Logger log = Logger.get(FlowHttpJober.class);

    private static final String HTTP_JOBER_FITABLE = "93f0b03c2ff94a46af5ace0088a8ce22";

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> contextData = getInputs(inputs);
        List<Map<String, Object>> outputs = fitableInvoke(contextData, HTTP_JOBER_FITABLE);
        log.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, HTTP_JOBER_FITABLE);
        return convertToFlowData(outputs, inputs.get(0));
    }
}

