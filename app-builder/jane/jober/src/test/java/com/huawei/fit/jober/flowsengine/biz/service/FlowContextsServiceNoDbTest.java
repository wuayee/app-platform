/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter.FlowDataConverter;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.From;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.nodes.Node;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 沒有db的FlowContextsService测试
 *
 * @author x00576283
 * @since 2024/4/24
 */
public class FlowContextsServiceNoDbTest {
    private TraceOwnerService traceOwnerService;
    private FlowContextsService flowContextsService;

    private FlowDefinitionRepo flowDefinitionRepo;

    @BeforeEach
    void setUp() {
        traceOwnerService = Mockito.mock(TraceOwnerService.class);
        flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
        QueryFlowContextPersistRepo queryFlowContextPersistRepo = Mockito.mock(QueryFlowContextPersistRepo.class);
        flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
        FlowContextRepo flowContextRepo = Mockito.mock(FlowContextRepo.class);
        FlowContextMessenger flowContextMessenger = Mockito.mock(FlowContextMessenger.class);
        FlowTraceRepo flowTraceRepo = Mockito.mock(FlowTraceRepo.class);
        FlowLocks flowLocks = Mockito.mock(FlowLocks.class);
        flowContextsService = new FlowContextsService(flowDefinitionRepo, flowContextRepo, flowContextMessenger,
                queryFlowContextPersistRepo, flowTraceRepo, null, flowLocks, traceOwnerService, new ArrayList<>(),
                null);
    }

    @Test
    @DisplayName("测试恢复一个异步jober")
    public void testResumeAsyncJober() {
        FlowNode mockFlowNode = mock(FlowNode.class);
        when(mockFlowNode.getMetaId()).thenReturn("jadeMockId");
        FlowJober mockFlowJober = mock(FlowJober.class);
        FlowDataConverter flowDataConverter = mock(FlowDataConverter.class);
        Map<String, Object> convertNullOutput = new HashMap<>();
        Map<String, Object> convertNullSecondLayerOutput = new HashMap<>();
        convertNullSecondLayerOutput.put("secondLayerK1", null);
        convertNullOutput.put("k1", null);
        convertNullOutput.put("k2", convertNullSecondLayerOutput);
        when(flowDataConverter.convertOutput(eq(null))).thenReturn(convertNullOutput);
        when(mockFlowJober.getConverter()).thenReturn(flowDataConverter);
        when(mockFlowNode.getJober()).thenReturn(mockFlowJober);
        From from = mock(From.class);
        FlowDefinition flowDefinition = mock(FlowDefinition.class);
        when(flowDefinition.convertToFlow(any(), any(), any())).thenReturn(from);
        Node node = mock(Node.class);
        String position = "state1";
        when(from.findNodeFromFlow(any(), eq(position))).thenReturn(node);
        when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(flowDefinition);
        when(flowDefinition.getFlowNode(anyString())).thenReturn(mockFlowNode);

        FlowData flowData = FlowData.builder()
                .operator("a")
                .startTime(LocalDateTime.now())
                .businessData(new HashMap<>())
                .contextData(new HashMap<>())
                .build();
        FlowContext<FlowData> flowContext = new FlowContext<>("streamId", "rootId", flowData,
                Collections.singleton("traceId"), "nodeId");
        flowContext.setPosition(position);
        flowContext.setStatus(FlowNodeStatus.PROCESSING);
        Map<String, Object> newBusinessData = new HashMap<>();
        Map<String, Object> secondLayerMap = new HashMap<>();
        secondLayerMap.put("secondLayerK1", "secondLayerV1");
        newBusinessData.put("k1", "v1");
        newBusinessData.put("k2", secondLayerMap);
        flowContextsService.resumeAsyncJob(
                Collections.singletonList(flowContext), Collections.singletonList(newBusinessData),
                getOperationContext());
        testResumeAsyncJoberAssertions(position, node, flowContext);
    }

    private static void testResumeAsyncJoberAssertions(String position, Node node, FlowContext<FlowData> flowContext) {
        ArgumentCaptor<List> captorPre = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List> captorAfter = ArgumentCaptor.forClass(List.class);
        verify(node).afterProcess(captorPre.capture(), captorAfter.capture());
        List<FlowContext<FlowData>> pre = captorPre.getValue();
        List<FlowContext<FlowData>> after = captorAfter.getValue();
        Assertions.assertEquals(1, pre.size());
        Assertions.assertEquals(FlowNodeStatus.PROCESSING, pre.get(0).getStatus());
        Assertions.assertEquals(1, after.size());
        Assertions.assertEquals(flowContext.getStreamId(), after.get(0).getStreamId());
        Assertions.assertEquals(flowContext.getRootId(), after.get(0).getRootId());
        Assertions.assertEquals(position, after.get(0).getPosition());
        Assertions.assertEquals(flowContext.getId(), after.get(0).getPrevious());
        Assertions.assertEquals(3, after.get(0).getData().getBusinessData().size());
    }

    @Test
    @Disabled
    @DisplayName("测试异步job失败能正常设置的场景")
    public void shouldSetJoberFailedWhenFailAsyncJobGivenError() {
        From from = mock(From.class);
        FlowDefinition flowDefinition = mock(FlowDefinition.class);
        when(flowDefinition.convertToFlow(any(), any(), any())).thenReturn(from);
        String position = "state1";
        Node node = mock(Node.class);
        when(from.findNodeFromFlow(any(), eq(position))).thenReturn(node);
        when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(flowDefinition);

        FlowData flowData = FlowData.builder()
                .operator("a")
                .startTime(LocalDateTime.now())
                .businessData(new HashMap<>())
                .contextData(new HashMap<>())
                .build();
        FlowContext<FlowData> flowContext = new FlowContext<>("streamId", "rootId", flowData, Collections.singleton("traceId"), "nodeId");
        flowContext.setPosition(position);
        flowContext.setStatus(FlowNodeStatus.PROCESSING);
        Map<String, Object> newBusinessData = new HashMap<>();
        newBusinessData.put("k1", "v1");
        JobberParamException expectException = new JobberParamException(INPUT_PARAM_IS_INVALID, "xxx");
        List<FlowContext<FlowData>> expectContexts = Collections.singletonList(flowContext);
        flowContextsService.failAsyncJob(expectContexts, expectException, getOperationContext());

        ArgumentCaptor<List> captorPre = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Exception> captorException = ArgumentCaptor.forClass(Exception.class);
        verify(node).setFailed(captorPre.capture(), captorException.capture());
        List<FlowContext<FlowData>> pre = captorPre.getValue();
        Exception exception = captorException.getValue();
        Assertions.assertEquals(expectContexts, pre);
        Assertions.assertEquals(FlowNodeStatus.PROCESSING, pre.get(0).getStatus());
        Assertions.assertEquals(expectException, exception);
    }

    private OperationContext getOperationContext() {
        return OperationContext.custom().operator("xx").operatorIp("0.0.0.1").tenantId("tianzhou").build();
    }
}
