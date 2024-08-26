/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.InstanceService;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.entity.InstanceQueryFilter;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.waterflow.edatamate.dao.po.FlowNotificationPo;
import com.huawei.fit.waterflow.edatamate.enums.ScanStatus;
import com.huawei.fit.waterflow.edatamate.repo.FlowNotificationRepo;
import com.huawei.fit.waterflow.flowsengine.biz.service.entity.FlowTransCompletionInfo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowStartNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.broker.client.filter.route.FitableIdFilter;
import com.huawei.fitframework.exception.FitException;
import com.huawei.waterflow.edatamate.TaskInstanceService;

import com.alibaba.fastjson.JSON;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * TaskUpdater测试类
 *
 * @author yangxiangyu
 * @since 2024/8/14
 */
class TaskUpdaterTest {
    private static final String INSTANCE_FINISHED_TASK_GENERICABLE = "afc63686857d47cab4343ea1847f769f";
    private static DefaultFlowDefinitionRepo flowDefinitionRepo = Mockito.mock(DefaultFlowDefinitionRepo.class);

    private static FlowContextPersistRepo flowContextPersistRepo = Mockito.mock(FlowContextPersistRepo.class);

    private static FlowNotificationRepo flowNotificationRepo = Mockito.mock(FlowNotificationRepo.class);

    private static InstanceService instanceService = Mockito.mock(InstanceService.class);

    private static FlowLocks locks = Mockito.mock(FlowLocks.class);

    private static BrokerClient brokerClient = Mockito.mock(BrokerClient.class);

    private TaskUpdater taskUpdater;


    @BeforeEach
    void setUp() {
        taskUpdater = new TaskUpdater(flowDefinitionRepo, flowContextPersistRepo, flowNotificationRepo,
                instanceService, locks, brokerClient);
    }

    @Test
    @DisplayName("测试任务完成回调成功")
    public void testOnFlowTransCompletedSuccess() {
        FlowTransCompletionInfo completionInfo = FlowTransCompletionInfo
                .builder()
                .flowTransId("123")
                .status(FlowTraceStatus.ARCHIVED)
                .build();

        Map<String, Object> businessData = new HashMap<>();
        String taskId = "123";
        String taskInstanceId = "456";
        businessData.put("taskId", taskId);
        businessData.put("taskInstanceId", taskInstanceId);
        when(flowContextPersistRepo.getStreamIdByTransId(anyString())).thenReturn("456");
        FlowDefinition definition = Mockito.mock(FlowDefinition.class);
        when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(definition);
        FlowStartNode startNode = new FlowStartNode();
        startNode.setMetaId("start");
        when(definition.getFlowNode(FlowNodeType.START)).thenReturn(startNode);
        List<FlowContext<FlowData>> contexts = new ArrayList<>();
        FlowContext<FlowData> context = new FlowContext<>(null, null, null, new HashSet<>(), null);
        FlowData data = FlowData.builder().businessData(businessData).build();
        context.setData(data);
        contexts.add(context);
        Mockito.when(flowContextPersistRepo.findFinishedContextsPagedByTransId("123", "start", 1, 1))
                .thenReturn(contexts);

        Map<String, Object> extensions = new HashMap<>();
        extensions.put("scanStatus", ScanStatus.END.getCode());
        Map<String, String> info = new HashMap<>();
        info.put("flow_context_id", "123");
        info.put("status", "RUNNING");
        info.put("extensions", JSON.toJSONString(extensions));
        info.put("file_num", "1");
        info.put("processed_num", "2");
        info.put("progress_percent", "0.0");
        Instance instance = new Instance();
        instance.setInfo(info);
        RangedResultSet<Instance> list = new RangedResultSet<>();
        list.setResults(Collections.singletonList(instance));
        when(instanceService.list(anyString(), any(InstanceQueryFilter.class), anyLong(), anyInt(), anyBoolean(),
                any(OperationContext.class))).thenReturn(list);
        Lock lock = Mockito.mock(Lock.class);
        when(locks.getDistributedLock(anyString())).thenReturn(lock);

        taskUpdater.onFlowTransCompleted(completionInfo);

        // 校验回调2个算子
        verify(flowNotificationRepo, times(2)).create(anyString(), anyMap());

        // 校验更新一次任务实例
        verify(instanceService).list(anyString(), any(InstanceQueryFilter.class), anyLong(), anyInt(), anyBoolean(),
                any(OperationContext.class));
        verify(instanceService).patchTaskInstance(anyString(), anyString(), any(), any());
    }

    @Test
    @DisplayName("测试通知算子成功")
    public void testNotificationFitableSuccess() {
        Lock lock = Mockito.mock(Lock.class);
        when(locks.getDistributedLock("TASK_NOTIFY")).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        List<FlowNotificationPo> flowNotificationPos1 = new ArrayList<>();
        flowNotificationPos1.add(new FlowNotificationPo("123", "123", null, 0, null));
        List<FlowNotificationPo> flowNotificationPos2 = new ArrayList<>();
        when(flowNotificationRepo.findNextRetryList(any()))
                .thenReturn(flowNotificationPos1).thenReturn(flowNotificationPos2);

        Router router = Mockito.mock(Router.class);
        Invoker invoker = Mockito.mock(Invoker.class);
        when(brokerClient.getRouter(TaskInstanceService.class, INSTANCE_FINISHED_TASK_GENERICABLE)).thenReturn(router);
        when(router.route(any(FitableIdFilter.class))).thenReturn(invoker);
        when(invoker.timeout(anyLong(), any(TimeUnit.class))).thenReturn(invoker);

        taskUpdater.flowNotifyTask();

        verify(flowNotificationRepo, times(2)).findNextRetryList(any(LocalDateTime.class));
        verify(flowNotificationRepo).delete(anyString());
    }

    @Test
    @DisplayName("测试通知算子失败")
    public void testNotificationFitableFail() {
        Lock lock = Mockito.mock(Lock.class);
        when(locks.getDistributedLock("TASK_NOTIFY")).thenReturn(lock);
        when(lock.tryLock()).thenReturn(true);
        List<FlowNotificationPo> flowNotificationPos1 = new ArrayList<>();
        flowNotificationPos1.add(new FlowNotificationPo("123", "123", null, 0, null));
        List<FlowNotificationPo> flowNotificationPos2 = new ArrayList<>();
        when(flowNotificationRepo.findNextRetryList(any()))
                .thenReturn(flowNotificationPos1).thenReturn(flowNotificationPos2);
        when(brokerClient.getRouter(TaskInstanceService.class, INSTANCE_FINISHED_TASK_GENERICABLE))
                .thenThrow(new FitException("Notify failed"));

        taskUpdater.flowNotifyTask();

        verify(flowNotificationRepo).update(any(FlowNotificationPo.class));
    }
}