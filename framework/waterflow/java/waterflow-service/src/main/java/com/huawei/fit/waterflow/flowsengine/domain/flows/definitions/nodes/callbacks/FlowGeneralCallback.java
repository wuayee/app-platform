/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks;

import com.huawei.fit.jober.FlowCallbackService;
import com.huawei.fit.waterflow.common.Constant;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程定义节点通用型回调函数类
 *
 * @author 李哲峰
 * @since 2023/12/13
 */
public class FlowGeneralCallback extends FlowCallback {
    private static final Logger log = Logger.get(FlowGeneralCallback.class);

    private static final String GENERAL_CALLBACK_GENERICABLE = "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw";

    @Override
    protected void executeCallback(List<FlowContext<FlowData>> inputs) {
        log.info("[FlowGeneralCallback]: start to execute callback.");

        for (String fitableId : fitables) {
            brokerClient.getRouter(FlowCallbackService.class, GENERAL_CALLBACK_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(prepareContexts(inputs));
            log.info("[FlowGeneralCallback]: Remote invocation succeeded, fitable id is {}.", fitableId);
        }
    }

    private List<Map<String, Object>> prepareContexts(List<FlowContext<FlowData>> inputs) {
        return inputs.stream().map(cxt -> {
            Map<String, Object> businessData = cxt.getData().getBusinessData();
            if (Optional.ofNullable(filteredKeys).isPresent() && !filteredKeys.isEmpty()) {
                businessData.keySet().retainAll(filteredKeys);
            }
            return new HashMap<String, Object>() {
                {
                    put(Constant.FLOW_CONTEXT_ID_KEY, cxt.getId());
                    put(Constant.TRACE_ID_KEY, cxt.getTraceId());
                    put(Constant.NODE_ID_KEY, cxt.getPosition());
                    put(Constant.BUSINESS_DATA_KEY, businessData);
                    put(Constant.CONTEXT_DATA, cxt.getData().getContextData());
                    put("status", cxt.getStatus().name());
                    put("createAt", cxt.getCreateAt());
                    put("updateAt", cxt.getUpdateAt());
                    put("archivedAt", cxt.getArchivedAt());
                }
            };
        }).collect(Collectors.toList());
    }
}
