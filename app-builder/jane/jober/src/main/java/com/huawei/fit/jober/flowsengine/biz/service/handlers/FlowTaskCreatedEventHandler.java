/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service.handlers;

import static com.huawei.fit.jober.FlowExceptionService.HANDLE_EXCEPTION_GENERICABLE;
import static com.huawei.fit.waterflow.common.Constant.BUSINESS_DATA_KEY;
import static com.huawei.fit.waterflow.common.Constant.CONTEXT_DATA;
import static com.huawei.fit.waterflow.common.Constant.FLOWS_EVENT_HANDLER_EXECUTOR;
import static com.huawei.fit.waterflow.common.Constant.PASS_DATA;

import com.huawei.fit.jober.FlowExceptionService;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.events.FlowTaskCreatedEvent;
import com.huawei.fit.jober.flowsengine.domain.flows.utils.FlowExecuteInfoUtil;
import com.huawei.fit.jober.flowsengine.manual.operation.OperatorFactory;
import com.huawei.fit.jober.flowsengine.manual.operation.operator.Operator;
import com.huawei.fit.jober.flowsengine.utils.FlowUtil;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.events.FlowTaskCreatedEvent;
import com.huawei.fitframework.annotation.Asynchronous;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 手动任务事件处理类
 *
 * @author 00693950
 * @since 2023/9/14
 */
@Component
public class FlowTaskCreatedEventHandler implements EventHandler<FlowTaskCreatedEvent> {
    private static final Logger log = Logger.get(FlowTaskCreatedEventHandler.class);

    private final FlowContextPersistRepo flowContextPersistRepo;

    private final FlowDefinitionRepo flowDefinitionRepo;

    private final BrokerClient brokerClient;

    private static final String TASK_EXECUTE_INFO_TYPE = "task";

    public FlowTaskCreatedEventHandler(FlowContextPersistRepo flowContextPersistRepo,
            FlowDefinitionRepo flowDefinitionRepo, BrokerClient brokerClient) {
        this.flowContextPersistRepo = flowContextPersistRepo;
        this.flowDefinitionRepo = flowDefinitionRepo;
        this.brokerClient = brokerClient;
    }

    /**
     * 处理流程引擎发布的事件，创建人工任务
     * 流程引擎发布的事件中，包含一个批次的contexts ID，该批次是用户配置自定义过滤器或者默认过滤器产生的
     * 这一批次的contexts只生成一个人工任务，任务人工必须能够承载这批次contexts中的业务数据
     * 这一批次的contexts产生的单个人工任务，每个context相当于任务中的一行数据，这行数据要与context ID做1:1关联
     * 流程恢复执行时，由业务发送一批更新过的context ID以及对应的属性变化值触发流程恢复执行
     *
     * @param eventData 流程引擎发布的事件数据
     */
    @Override
    @Asynchronous(executor = FLOWS_EVENT_HANDLER_EXECUTOR)
    public void handleEvent(FlowTaskCreatedEvent eventData) {
        log.info("[FlowTaskCreatedEventHandler]: start to handle event.");
        List<String> contextIds = eventData.getFlowContextId();
        List<FlowContext<FlowData>> contexts = flowContextPersistRepo.getByIds(contextIds);

        String streamId = eventData.getStreamId();
        FlowDefinition flowDefinition = flowDefinitionRepo.findByStreamId(streamId);
        FlowTask task = flowDefinition.getFlowNode(eventData.getNodeId()).getTask();
        String type = task.getTaskType().getSource();

        List<FlowContext<FlowData>> convertedContexts = this.convertFlowContext(contexts, task);
        Operator operator = OperatorFactory.getOperator(type, brokerClient);
        try {
            operator.operate(convertedContexts, task);
        } catch (FitException e) {
            for (String fitableId : task.getExceptionFitables()) {
                this.brokerClient.getRouter(FlowExceptionService.class, HANDLE_EXCEPTION_GENERICABLE)
                        .route(new FitableIdFilter(fitableId))
                        .invoke(eventData.getNodeId(), getFlowData(convertedContexts), e.getMessage());
            }
            log.error("Caught a throwable during the task handling. TaskId is {}. Caused by {}", task.getTaskId(),
                    e.getMessage());
        }
    }

    private List<FlowContext<FlowData>> convertFlowContext(List<FlowContext<FlowData>> contexts, FlowTask task) {
        FlowDataConverter taskConverter = task.getConverter();
        if (Objects.isNull(taskConverter)) {
            return contexts;
        }
        contexts.forEach(context -> {
            FlowData flowData = context.getData();
            Map<String, Object> newInputMap = taskConverter.convertInput(flowData.getBusinessData());
            Optional.of(flowData)
                    .map(FlowData::getContextData)
                    .map(contextData -> contextData.get("nodeMetaId"))
                    .map(Object::toString)
                    .ifPresent(nodeMetaId -> FlowExecuteInfoUtil.addInputMap2ExecuteInfoMap(flowData, newInputMap,
                            nodeMetaId, TASK_EXECUTE_INFO_TYPE));
            flowData.setBusinessData(FlowUtil.mergeMaps(flowData.getBusinessData(), newInputMap));
        });
        return contexts;
    }

    private List<Map<String, Object>> getFlowData(List<FlowContext<FlowData>> flowContexts) {
        return flowContexts.stream().map(cxt -> new HashMap<String, Object>() {
            {
                put(BUSINESS_DATA_KEY, cxt.getData().getBusinessData());
                put(CONTEXT_DATA, cxt.getData().getContextData());
                put(PASS_DATA, cxt.getData().getPassData());
            }
        }).collect(Collectors.toList());
    }
}
