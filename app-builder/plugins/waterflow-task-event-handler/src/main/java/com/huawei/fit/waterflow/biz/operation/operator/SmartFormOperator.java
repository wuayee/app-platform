/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.operation.operator;

import static com.huawei.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.waterflow.common.Constant.FLOW_CONTEXT_ID_KEY;
import static com.huawei.fit.waterflow.common.Constant.NODE_ID_KEY;
import static com.huawei.fit.waterflow.common.Constant.TRACE_ID_KEY;

import com.huawei.fit.jober.FlowSmartFormService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 智能表单Operator
 *
 * @author 李哲峰
 * @since 2023/12/14
 */
public class SmartFormOperator implements Operator {
    private static final Logger log = Logger.get(SmartFormOperator.class);

    private static final String HANDLE_SMART_FORM_GENERICABLE = "htctmizg0mydwnt2ttbbp8jlgo2e9e0w";

    private static final String HANDLE_SMART_FORM_FITABLE = "qz90ufu144m607hfud1ecbk0dnq3xavd";

    private final BrokerClient brokerClient;

    public SmartFormOperator(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
    }

    @Override
    public void operate(List<FlowContext<FlowData>> contexts, FlowTask task) {
        List<Map<String, Object>> inputs = contexts.stream().map(cxt -> new HashMap<String, Object>() {
            {
                put(FLOW_CONTEXT_ID_KEY, cxt.getId());
                put(NODE_ID_KEY, cxt.getPosition());
                put(TRACE_ID_KEY, cxt.getTraceId());
                put(BUSINESS_DATA_KEY, cxt.getData().getBusinessData());
            }
        }).collect(Collectors.toList());

        try {
            brokerClient.getRouter(FlowSmartFormService.class, HANDLE_SMART_FORM_GENERICABLE)
                    .route(new FitableIdFilter(HANDLE_SMART_FORM_FITABLE))
                    .invoke(inputs, task.getTaskId());
        } catch (FitException e) {
            log.error("Caught a throwable during a remote invocation, fitableId is {}. Caused by {}",
                    HANDLE_SMART_FORM_FITABLE, e.getMessage());
            throw new JobberException(ErrorCodes.FLOW_HANDLE_SMART_FORM_FAILED);
        }
    }

    public BrokerClient getBrokerClient() {
        return brokerClient;
    }
}
