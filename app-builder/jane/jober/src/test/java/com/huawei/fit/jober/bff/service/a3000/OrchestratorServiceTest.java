/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.bff.service.a3000;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.TaskService;
import com.huawei.fit.jober.common.RangeResult;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.InstanceQueryFilter;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.flowsengine.biz.service.FlowContextsService;
import com.huawei.fit.jober.flowsengine.biz.service.FlowsService;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowOfferId;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowTrans;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import com.huawei.fit.jober.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.jober.flowsengine.persist.mapper.FlowTraceMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * a3000相关测试
 *
 * @author yangxiangyu
 * @since 2023/11/28
 */
public class OrchestratorServiceTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InstanceService instanceService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowContextsService flowContextsService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowContextMapper flowContextMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowTraceMapper flowTraceMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowContextPersistRepo flowContextPersistRepo;

    @InjectMocks
    private OrchestratorService orchestratorService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowsService flowsService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private QueryFlowContextPersistRepo queryFlowContextPersistRepo;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowTraceRepo flowTraceRepo;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TaskService taskService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DefaultFlowDefinitionRepo flowDefinitionRepo;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private FlowLocks locks;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TaskUpdater taskUpdater;

    @BeforeEach
    public void setUp() {
        instanceService = mock(InstanceService.class);
        flowContextsService = mock(FlowContextsService.class);
        flowsService = mock(FlowsService.class);
        flowContextMapper = mock(FlowContextMapper.class);
        flowTraceMapper = mock(FlowTraceMapper.class);
        flowTraceRepo = mock(FlowTraceRepo.class);
        taskService = mock(TaskService.class);
        flowDefinitionRepo = mock(DefaultFlowDefinitionRepo.class);
        locks = mock(FlowLocks.class);
        taskUpdater = mock(TaskUpdater.class);
        orchestratorService = new OrchestratorService(instanceService, taskService, flowContextsService, flowsService,
                flowContextPersistRepo, queryFlowContextPersistRepo, flowDefinitionRepo, flowTraceRepo, locks,
                taskUpdater);
    }

    @Test
    @DisplayName("测试启动任务成功")
    public void testStartTask() {
        // arrange
        Instance instance = new Instance();
        Map<String, String> map = new HashMap<>();
        map.put("flow_id", "a8f6b478e2f04efca2bcdb8d5923c7a9");
        map.put("flow_version", "1.0.0");
        map.put("flow_config", "{\"id\": 1, \"businessData\": {}}");
        instance.setInfo(map);
        RangedResultSet<Instance> resultSet = new RangedResultSet<>(Collections.singletonList(instance),
                new RangeResult(0, 1, 1));

        // mock
        Mockito.when(
                instanceService.list(anyString(), any(InstanceQueryFilter.class), anyLong(), anyInt(), anyBoolean(),
                        any(OperationContext.class))).thenReturn(resultSet);
        Mockito.when(flowContextsService.startFlows(anyString(), anyString(), anyString()))
                .thenReturn(new FlowOfferId(new FlowTrans("a8f6b478"), "b8f6b478"));

        // act & assert
        String taskId = "123";
        String instanceId = "f9dad40c16e5478da944e162301c20c0";
        String flowTransId = orchestratorService.startTask(taskId, instanceId);
        Assertions.assertEquals("a8f6b478", flowTransId);
    }

    // @Test
    // @Disabled
    // @DisplayName("测试终止任务成功")
    // public void testTerminateTask() {
    //     // arrange
    //     String taskId = "123";
    //     String instanceId = "f9dad40c16e5478da944e162301c20c0";
    //     Instance instance = new Instance();
    //     Map<String, String> map = new HashMap<>();
    //     map.put("flow_id", "a8f6b478e2f04efca2bcdb8d5923c7a9");
    //     map.put("flow_version", "1.0.0");
    //     map.put("flow_config", "{\"id\": 1}");
    //     map.put("flow_context_id", "944e162301c20c0");
    //     instance.setInfo(map);
    //     RangedResultSet<Instance> resultSet = new RangedResultSet<>(Collections.singletonList(instance),
    //         new RangeResult(0, 1, 1));
    //
    //     FlowContextPO flowContextPO = FlowContextPO.builder().build();
    //     flowContextPO.setTraceId("traceId");
    //     flowContextPO.setPositionId("curNodes");
    //     List<FlowContextPO> flowContexts = Collections.singletonList(flowContextPO);
    //
    //     // mock
    //     Mockito.when(instanceService.list(anyString(), any(InstanceQueryFilter.class), anyLong(), anyInt(),
    //         any(OperationContext.class))).thenReturn(resultSet);
    //     Mockito.when(flowContextMapper.findByTransaction(anyString())).thenReturn(flowContexts);
    //     Mockito.when(flowTraceMapper.find(anyString())).thenReturn(FlowTracePO.builder().build());
    //
    //     // act & assert
    //     boolean result = orchestratorService.terminateTask(taskId, instanceId);
    //     Assertions.assertTrue(result);
    // }

    @Test
    @Disabled
    @DisplayName("测试自动生成ohScript代码成功")
    public void testGetScript() {
        List<String> conditionFitables = new ArrayList<>();
        conditionFitables.add("com.huawei.eDataMate.operators.pdf_extractor_plugin");
        conditionFitables.add("com.huawei.eDataMate.operators.word_extractor_plugin");
        List<String> fitableIdsSerial = new ArrayList<>();
        fitableIdsSerial.add("3333");
        fitableIdsSerial.add("4444");
        List<List<String>> fitables = new ArrayList<>();
        fitables.add(conditionFitables);
        fitables.add(fitableIdsSerial);

        String expectedConditionScript =
                "var type = ext::context.get(0).get(\"passData\").get(\"meta\").get(\"fileType\");"
                        + "if(type==\\\"pdf\\\"){\\n\" + \"    let context1 = entity{\\n\"\n"
                        + "                + \"        .id = \\\"com.huawei.eDataMate.operators.pdf_extractor_plugin"
                        + "\\\";\\n\" + \"    };\\n\"\n"
                        + "                + \"    let f1 = fit::handleTask(context1);\\n\" + \"    ext::context >> f1"
                        + "\\n\" + \"}\"" + "if (type == \\\"word\\\") {\\n\" + \"    let context1 = entity{\\n\"\n"
                        + "                + \"        .id = \\\"com.huawei.eDataMate.operators.word_extractor_plugin"
                        + "\\\";\\n\" + \"    };\\n\"\n"
                        + "                + \"    let f1 = fit::handleTask(context1);\\n\" + \"    ext::context >> f1"
                        + "\\n\" + \"}";

        String expectedScriptSerial = "let context1 = entity{\n" + "    .id = \"11111111\";\n" + "};\n"
                + "let f1 = fit::handleTask(context1)\n" + "\n" + "let context2 = entity{\n"
                + "    .id = \"222222222\";\n" + "};\n" + "let f2 = fit::handleTask(context2)\n" + "\n"
                + "ext::context >> f1 >> f2";
        List<String> expected = new ArrayList<>();
        expected.add(expectedConditionScript);
        expected.add(expectedScriptSerial);
        Mockito.when(flowsService.getScript(any())).thenReturn(expectedScriptSerial);
        List<String> actual = orchestratorService.getScript(fitables);
        Assertions.assertEquals(actual, expected);
    }
}