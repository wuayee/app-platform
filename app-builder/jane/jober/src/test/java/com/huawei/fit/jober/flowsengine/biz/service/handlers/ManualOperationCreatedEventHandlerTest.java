/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service.handlers;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.FlowSmartFormService;
import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.TaskService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.entity.task.Task;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.entity.task.TaskSource;
import com.huawei.fit.jober.flowsengine.biz.service.TraceOwnerService;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.FlowStateNode;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowTaskType;
import com.huawei.fit.jober.flowsengine.domain.flows.events.FlowTaskCreatedEvent;
import com.huawei.fit.jober.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.jober.flowsengine.persist.po.FlowContextPO;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;

import com.alibaba.fastjson.JSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link FlowTaskCreatedEventHandler} 测试类
 *
 * @author 00693950
 * @since 2023/9/18
 */
class ManualOperationCreatedEventHandlerTest extends DatabaseBaseTest {
    private static final String QUERY_TASK_GENERICABLE = "5a7683b3b6ac495198efc492790a3a5f";

    private static final String CREATE_TASK_INSTANCE_GENERICABLE = "f1b88d9eb48b48959365a24e27dabb80";

    private static final String HANDLE_SMART_FORM_GENERICABLE = "htctmizg0mydwnt2ttbbp8jlgo2e9e0w";

    private FlowDefinition generateFlowDefinition(FlowTaskType taskType) {
        Map<String, FlowNode> nodeMap = new HashMap<>();
        FlowNode flowNode = new FlowStateNode();
        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("title", "PM审批");
        propertiesMap.put("created_by", "{{creator}}");
        propertiesMap.put("owner", "{{owner1}}");

        FlowTask task = new FlowTask("taskId", taskType, Collections.singleton("exceptionFitable"), propertiesMap);
        flowNode.setTask(task);
        nodeMap.put("nodeId", flowNode);
        return FlowDefinition.builder().nodeMap(nodeMap).build();
    }

    private List<FlowContextPO> generatePO() {
        HashMap<String, Object> businessData = new HashMap<>();
        businessData.put("description", "描述信息");
        businessData.put("third_party_data", "第三方系统数据");
        businessData.put("creator", "yyk");
        businessData.put("owner1", "yyk_owner");
        businessData.put("priority", "urgent");
        businessData.put("statue", "approved");
        businessData.put("application", "tianzhou");
        FlowData flowData = FlowData.builder()
                .operator("yyk")
                .startTime(LocalDateTime.now())
                .businessData(businessData)
                .contextData(new HashMap<>())
                .build();
        FlowContextPO contextPO = FlowContextPO.builder()
                .contextId("flowContextId")
                .flowData(JSON.toJSONString(flowData))
                .traceId("traceId")
                .status(String.valueOf(ARCHIVED))
                .build();
        return Collections.singletonList(contextPO);
    }

    @Nested
    @DisplayName("测试手动执行任务事件的处理")
    class TestManualOperationEventHandler {
        private FlowTaskCreatedEventHandler handler;

        private FlowContextMapper flowContextMapper;

        private FlowTraceRepo flowTraceRepo;

        private FlowRetryRepo flowRetryRepo;

        private FlowDefinitionRepo flowDefinitionRepo;

        private BrokerClient brokerClient;

        private TraceOwnerService traceOwnerService;

        @BeforeEach
        void setUp() {
            flowContextMapper = Mockito.mock(FlowContextMapper.class);
            flowTraceRepo = Mockito.mock(FlowTraceRepo.class);
            flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
            traceOwnerService = Mockito.mock(TraceOwnerService.class);
            Integer defaultLimitation = 5;
            boolean useLimit = false;
            FlowContextPersistRepo flowContextPersistRepo = new FlowContextPersistRepo(flowContextMapper, flowTraceRepo,
                    flowRetryRepo, traceOwnerService, defaultLimitation, useLimit);
            flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
            brokerClient = Mockito.mock(BrokerClient.class);
            handler = new FlowTaskCreatedEventHandler(flowContextPersistRepo, flowDefinitionRepo, brokerClient);
        }

        @Test
        @DisplayName("获取手动任务事件创建任务实例成功")
        void givenRightManualOperationEventThenHandleSuccessfully() throws Throwable {
            FlowTaskCreatedEvent event = new FlowTaskCreatedEvent(Collections.singletonList("flowContextId"),
                    "metaId-version", "nodeId", "publisher");
            when(flowContextMapper.findByContextIdList(any())).thenReturn(generatePO());
            when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(
                    generateFlowDefinition(FlowTaskType.APPROVING_TASK));

            Invoker taskInvoker = Mockito.mock(Invoker.class);
            Invoker instanceInvoker = Mockito.mock(Invoker.class);
            Task task = generateTask();
            setupRouterMock(brokerClient, TaskService.class, QUERY_TASK_GENERICABLE, taskInvoker, task);
            setupRouterMock(brokerClient, InstanceService.class, CREATE_TASK_INSTANCE_GENERICABLE, instanceInvoker,
                    null);

            handler.handleEvent(event);
        }

        @Test
        @DisplayName("获取智能表单任务事件创建任务实例成功")
        void givenSmartFormManualOperationEventThenHandleSuccessfully() throws Throwable {
            FlowTaskCreatedEvent event = new FlowTaskCreatedEvent(Collections.singletonList("flowContextId"),
                    "metaId-version", "nodeId", "publisher");
            when(flowContextMapper.findByContextIdList(any())).thenReturn(generatePO());
            when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(
                    generateFlowDefinition(FlowTaskType.AIPP_SMART_FORM));

            Invoker smartFormInvoker = Mockito.mock(Invoker.class);
            setupRouterMock(brokerClient, FlowSmartFormService.class, HANDLE_SMART_FORM_GENERICABLE, smartFormInvoker,
                    null);

            handler.handleEvent(event);
            verify(smartFormInvoker, times(1)).invoke(anyList(), anyString());
        }

        @Test
        @DisplayName("获取智能表单任务事件创建任务实例处理异常成功")
        void givenSmartFormManualOperationEventThenHandleExceptionSuccessfully() throws Throwable {
            FlowTaskCreatedEvent event = new FlowTaskCreatedEvent(Collections.singletonList("flowContextId"),
                    "metaId-version", "nodeId", "publisher");
            when(flowContextMapper.findByContextIdList(any())).thenReturn(generatePO());
            when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(
                    generateFlowDefinition(FlowTaskType.AIPP_SMART_FORM));

            Invoker smartFormInvoker = Mockito.mock(Invoker.class);
            setupRouterMockWithException(brokerClient, smartFormInvoker,
                    new JobberException(ErrorCodes.FLOW_HANDLE_SMART_FORM_FAILED));

            handler.handleEvent(event);
            verify(smartFormInvoker, times(1)).invoke(anyList(), anyString());
            verify(smartFormInvoker, times(1)).invoke(anyString(), anyList(), anyString());
        }

        private void setupRouterMock(BrokerClient brokerClient, Class<?> serviceClass, String genericableId,
                Invoker invoker, Object result) throws Throwable {
            Router router = Mockito.mock(Router.class);
            when(brokerClient.getRouter(serviceClass, genericableId)).thenReturn(router);
            when(router.route(any())).thenReturn(invoker);
            when(invoker.invoke(any())).thenReturn(result);
        }

        private void setupRouterMockWithException(BrokerClient brokerClient, Invoker invoker, Throwable throwable)
                throws Throwable {
            Router router = Mockito.mock(Router.class);
            when(brokerClient.getRouter(any(), anyString())).thenReturn(router);
            when(router.route(any())).thenReturn(invoker);
            when(invoker.invoke(anyList(), anyString())).thenThrow(throwable);
            when(invoker.invoke(anyString(), anyList(), anyString())).thenReturn(null);
        }

        private Task generateTask() {
            List<Task> results = createTask();
            return results.get(0);
        }

        private List<Task> createTask() {
            Task task = new Task();
            List<TaskProperty> properties = Stream.of("title", "id", "status", "owner", "third_party_data",
                            "created_date", "modified_by", "priority", "finish_time", "tag", "risk", "description",
                            "created_by", "decomposed_from", "modified_date", "target_url", "progress_feedback")
                    .map(propertyName -> {
                        TaskProperty taskProperty = new TaskProperty();
                        taskProperty.setName(propertyName);
                        return taskProperty;
                    })
                    .collect(Collectors.toList());
            task.setProperties(properties);
            task.setSources(Collections.singletonList(createTaskSource()));
            return Collections.singletonList(task);
        }

        private TaskSource createTaskSource() {
            TaskSource taskSource = new TaskSource();
            taskSource.setId("sourceId");
            return taskSource;
        }
    }
}