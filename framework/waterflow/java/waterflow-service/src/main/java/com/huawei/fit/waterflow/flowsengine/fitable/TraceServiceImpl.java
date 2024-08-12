/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.fitable;

import static com.huawei.fit.jober.FlowPublishService.PUBLISH_NODE_INFO_GENERICABLE;
import static com.huawei.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.waterflow.common.Constant.CONTEXT_DATA;
import static com.huawei.fit.waterflow.common.Constant.NODE_ID_KEY;
import static com.huawei.fit.waterflow.common.Constant.TRACE_ID_KEY;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.FlowCallbackService;
import com.huawei.fit.jober.FlowExceptionService;
import com.huawei.fit.jober.FlowPublishService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.FlowNodePublishInfo;
import com.huawei.fit.jober.entity.FlowPublishContext;
import com.huawei.fit.waterflow.flowsengine.biz.service.FlowsService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 流程实例回调关于流程信息推送的实现。
 *
 * @author 陈镕希
 * @since 2024-05-24
 */
@Component
public class TraceServiceImpl implements FlowCallbackService, FlowExceptionService {
    private final BrokerClient brokerClient;

    private final FlowsService flowsService;

    /**
     * 流程实例回调构造方法
     *
     * @param brokerClient 框架调度的 {@link BrokerClient}
     * @param flowsService 流程定义相关服务的 {@link FlowsService}。
     */
    public TraceServiceImpl(BrokerClient brokerClient, FlowsService flowsService) {
        this.brokerClient = brokerClient;
        this.flowsService = flowsService;
    }

    private static FlowNodePublishInfo constructFlowNodePublishInfo(Map<String, Object> context, String nodeId,
            String errorMessage, String status) {
        Map<String, Object> businessData = getValueOfSpecifyKey(context, BUSINESS_DATA_KEY);
        Map<String, Object> contextData = getValueOfSpecifyKey(context, CONTEXT_DATA);
        Set<String> traces = cast(context.get(TRACE_ID_KEY));
        FlowPublishContext flowContext = new FlowPublishContext(cast(traces.toArray()[0]), status,
                cast(context.get("createAt")), cast(context.get("updateAt")), cast(context.get("archivedAt")));
        String flowDefinitionId = cast(contextData.get("flowDefinitionId"));
        FlowNodePublishInfo flowNodePublishInfo = new FlowNodePublishInfo();
        flowNodePublishInfo.setFlowDefinitionId(flowDefinitionId);
        flowNodePublishInfo.setNodeId(nodeId);
        flowNodePublishInfo.setNodeType(cast(contextData.get("nodeType")));
        flowNodePublishInfo.setBusinessData(businessData);
        flowNodePublishInfo.setFlowContext(flowContext);
        flowNodePublishInfo.setErrorMsg(errorMessage);
        return flowNodePublishInfo;
    }

    private static <T> T getValueOfSpecifyKey(Map<String, Object> map, String specifyKey) {
        if (!map.containsKey(specifyKey)) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, specifyKey);
        }
        return cast(map.get(specifyKey));
    }

    @Fitable("com.huawei.fit.jober.fitable.FlowInfoCallback")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        if (contexts.isEmpty()) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "contexts");
        }
        contexts.forEach(context -> {
            String nodeId = getValueOfSpecifyKey(context, NODE_ID_KEY);
            FlowNodePublishInfo flowNodePublishInfo = constructFlowNodePublishInfo(context, nodeId, StringUtils.EMPTY,
                    FlowNodeStatus.ARCHIVED.name());
            publishNodeInfo(flowNodePublishInfo);
        });
    }

    @Fitable("com.huawei.fit.jober.fitable.FlowInfoException")
    @Override
    public void handleException(String nodeId, List<Map<String, Object>> contexts, String errorMessage) {
        if (contexts.isEmpty()) {
            throw new JobberException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "contexts");
        }
        contexts.forEach(context -> {
            // 由于notify之后才统一改的context的status为ERROR，所以这里需要手动设置一下
            context.put("status", FlowNodeStatus.ERROR.name());
            FlowNodePublishInfo flowNodePublishInfo = constructFlowNodePublishInfo(context, nodeId, errorMessage,
                    cast(context.get("status")));
            publishNodeInfo(flowNodePublishInfo);
        });
    }

    /**
     * 发布节点信息.
     *
     * @param flowNodePublishInfo 流程节点信息对象.
     */
    public void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo) {
        String flowDefinitionId = flowNodePublishInfo.getFlowDefinitionId();
        FlowDefinition flowDefinition = flowsService.findFlowsById(flowDefinitionId, OperationContext.custom().build());
        for (String fitableId : flowDefinition.getPublishNodeFitables()) {
            this.brokerClient.getRouter(FlowPublishService.class, PUBLISH_NODE_INFO_GENERICABLE)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(flowNodePublishInfo);
        }
    }
}
