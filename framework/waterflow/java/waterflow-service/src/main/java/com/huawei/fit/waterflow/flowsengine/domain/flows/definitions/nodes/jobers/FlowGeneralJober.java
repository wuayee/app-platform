/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers;

import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fitframework.log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FlowNGeneralJober
 * 流程通用接口
 *
 * @author 杨祥宇
 * @since 2023/10/09
 */
public class FlowGeneralJober extends FlowJober {
    private static final Logger log = Logger.get(FlowGeneralJober.class);

    @Override
    protected List<FlowData> executeJober(List<FlowData> inputs) {
        List<Map<String, Object>> contextData = getInputs(inputs);
        List<Map<String, Object>> outputEntities = new ArrayList<>();
        for (String fitableId : fitables) {
            outputEntities = fitableInvoke(contextData, fitableId);
            log.info("Remote invoke success,nodeId: {}, fitable id is {}.", this.nodeMetaId, fitableId);
        }
        return convertToFlowData(outputEntities, inputs.get(0));
    }
}
