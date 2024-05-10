/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service;

import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.READY;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static com.huawei.fit.jober.flowsengine.domain.flows.enums.ProcessType.PROCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.common.utils.UUIDUtil;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowRetry;
import com.huawei.fit.jober.flowsengine.domain.flows.context.FlowTrace;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.jober.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.jober.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.From;
import com.huawei.fit.jober.flowsengine.domain.flows.streams.To;
import com.huawei.fit.jober.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.jober.flowsengine.persist.po.FlowContextPO;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * {@link FlowContextsService}对应测试类
 *
 * @author 00693950
 * @since 2023/11/6
 */
class FlowContextsServiceTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    @Nested
    @DisplayName("测试流程实例service")
    class FlowContextServiceTest extends DatabaseBaseTest {
        private static final String SQL_FILE = "handler/flowContext/saveData.sql";

        private static final String FILE_PATH_PREFIX = "flows/services/";

        private FlowContextsService flowContextsService;

        private FlowContextRepo flowContextRepo;

        private FlowContextMessenger flowContextMessenger;

        private FlowLocks flowLocks;

        private FlowTraceRepo flowTraceRepo;

        private FlowRetryRepo flowRetryRepo;

        private QueryFlowContextPersistRepo queryFlowContextPersistRepo;

        private FlowDefinitionRepo flowDefinitionRepo;

        @BeforeEach
        void setUp() {
            FlowContextMapper flowContextMapper = sqlSessionManager.openSession(true)
                    .getMapper(FlowContextMapper.class);
            queryFlowContextPersistRepo = Mockito.mock(QueryFlowContextPersistRepo.class);
            flowDefinitionRepo = Mockito.mock(FlowDefinitionRepo.class);
            flowContextRepo = Mockito.mock(FlowContextRepo.class);
            flowContextMessenger = Mockito.mock(FlowContextMessenger.class);
            flowTraceRepo = Mockito.mock(FlowTraceRepo.class);
            flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
            flowLocks = Mockito.mock(FlowLocks.class);

            flowContextsService = new FlowContextsService(flowDefinitionRepo, flowContextRepo, flowContextMessenger,
                    queryFlowContextPersistRepo, flowTraceRepo, flowRetryRepo, flowLocks, null, null);
        }

        @Test
        @Disabled
        @DisplayName("测试单个context运行状态ARCHIVED")
        public void testGetFlowCompletionByTraceIdStatueArchived() {
            List<FlowTrace> flowTrace = getFlowTrace();
            flowTrace.forEach(f -> {
                f.getContextPool().add("traceId");
            });
            when(flowTraceRepo.findTraceByIdList(anyList())).thenReturn(flowTrace);
            when(queryFlowContextPersistRepo.findByContextIdList(anyList())).thenReturn(getContextList());
            String jsonData = getJsonData(FILE_PATH_PREFIX + "flows_calculate_completeness_with_state_node.json");
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            when(flowDefinitionRepo.findByStreamIdList(anyList())).thenReturn(
                    Collections.singletonList(flowDefinition));

            Map<String, Map<String, Object>> flowCompleteness = flowContextsService.getFlowCompleteness(
                    Collections.singletonList("traceId"), false, getOperationContext());
            Map<String, Object> result = flowCompleteness.get(flowTrace.get(0).getId());

            assertEquals("100.00", result.get("percentage"));
            assertEquals("ARCHIVED", result.get("status"));
        }

        @Test
        @Disabled
        @DisplayName("测试单个context运行状态ERROR")
        public void testGetFlowCompletionByTraceIdStatusError() {
            String jsonData = getJsonData(FILE_PATH_PREFIX + "flows_calculate_completeness_with_state_node.json");
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowContextPO> contextList = getContextList();
            contextList.get(2).setStatus("ERROR");
            List<FlowTrace> flowTrace = getFlowTrace();
            flowTrace.forEach(f -> {
                f.setStreamId(flowDefinition.getStreamId());
                List<String> collect = contextList.stream()
                        .map(FlowContextPO::getContextId)
                        .collect(Collectors.toList());
                f.getContextPool().addAll(collect);
            });
            when(flowTraceRepo.findTraceByIdList(anyList())).thenReturn(flowTrace);
            when(queryFlowContextPersistRepo.findByContextIdList(anyList())).thenReturn(contextList);
            when(flowDefinitionRepo.findByStreamIdList(anyList())).thenReturn(
                    Collections.singletonList(flowDefinition));

            Map<String, Map<String, Object>> flowCompleteness = flowContextsService.getFlowCompleteness(
                    Collections.singletonList("traceId"), false, getOperationContext());
            Map<String, Object> result = flowCompleteness.get(flowTrace.get(0).getId());

            assertEquals("66.67", result.get("percentage"));
            assertEquals("ERROR", result.get("status"));
            assertEquals("结束节点", ObjectUtils.<List<String>>cast(result.get("errorNode")).get(0));
        }

        @Test
        @Disabled
        @DisplayName("测试单个context运行状态Running")
        public void testGetFlowCompletionByTraceIdStatusRunning() {
            String jsonData = getJsonData(FILE_PATH_PREFIX + "flows_calculate_completeness_with_state_node.json");
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowContextPO> contextList = getContextList();
            contextList.get(2).setStatus("NEW");
            List<FlowTrace> flowTrace = getFlowTrace();
            flowTrace.forEach(f -> {
                f.setStreamId(flowDefinition.getStreamId());
                List<String> collect = contextList.stream()
                        .map(FlowContextPO::getContextId)
                        .collect(Collectors.toList());
                f.getContextPool().addAll(collect);
            });
            when(flowTraceRepo.findTraceByIdList(anyList())).thenReturn(flowTrace);
            when(queryFlowContextPersistRepo.findByContextIdList(anyList())).thenReturn(contextList);
            when(flowDefinitionRepo.findByStreamIdList(anyList())).thenReturn(
                    Collections.singletonList(flowDefinition));

            Map<String, Map<String, Object>> flowCompleteness = flowContextsService.getFlowCompleteness(
                    Collections.singletonList("traceId"), false, getOperationContext());
            Map<String, Object> result = flowCompleteness.get(flowTrace.get(0).getId());

            assertEquals("66.67", result.get("percentage"));
            assertEquals("RUNNING", result.get("status"));
        }

        @Test
        @Disabled
        @DisplayName("测试单个context经过条件节点运行状态Archived")
        public void testMContextGetFlowCompletionByTraceIdStatusArchived() {
            String jsonData = getJsonData(FILE_PATH_PREFIX + "flows_calculate_completeness_with_condition_node.json");
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowTrace> flowTrace = getFlowTrace();
            flowTrace.forEach(f -> {
                f.getContextPool().add("traceId");
            });
            when(flowTraceRepo.findTraceByIdList(anyList())).thenReturn(flowTrace);
            when(queryFlowContextPersistRepo.findByContextIdList(anyList())).thenReturn(getContextList());
            when(flowDefinitionRepo.findByStreamIdList(anyList())).thenReturn(
                    Collections.singletonList(flowDefinition));

            Map<String, Map<String, Object>> flowCompleteness = flowContextsService.getFlowCompleteness(
                    Collections.singletonList("traceId"), false, getOperationContext());
            Map<String, Object> result = flowCompleteness.get(flowTrace.get(0).getId());

            assertEquals("100.00", result.get("percentage"));
            assertEquals("ARCHIVED", result.get("status"));
        }

        @Test
        @Disabled
        @DisplayName("测试单个context经过条件节点运行状态Running")
        public void testMContextGetFlowCompletionByTraceIdStatusRunning() {
            String jsonData = getJsonData(FILE_PATH_PREFIX + "flows_calculate_completeness_with_state_node.json");
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowContextPO> contextList = getConditionContextList();
            contextList.get(4).setStatus("NEW");
            contextList.get(5).setStatus("NEW");
            List<FlowTrace> flowTrace = getFlowTrace();
            flowTrace.forEach(f -> {
                f.setStreamId(flowDefinition.getStreamId());
                List<String> collect = contextList.stream()
                        .map(FlowContextPO::getContextId)
                        .collect(Collectors.toList());
                f.getContextPool().addAll(collect);
            });
            when(flowTraceRepo.findTraceByIdList(anyList())).thenReturn(flowTrace);
            when(queryFlowContextPersistRepo.findByContextIdList(anyList())).thenReturn(contextList);
            when(flowDefinitionRepo.findByStreamIdList(anyList())).thenReturn(
                    Collections.singletonList(flowDefinition));

            Map<String, Map<String, Object>> flowCompleteness = flowContextsService.getFlowCompleteness(
                    Collections.singletonList("traceId"), false, getOperationContext());
            Map<String, Object> result = flowCompleteness.get(flowTrace.get(0).getId());

            assertEquals(String.format(Locale.ROOT, "%.2f", (double) 5 / 7 * 100), result.get("percentage"));
            assertEquals("RUNNING", result.get("status"));
        }

        @Test
        @Disabled
        @DisplayName("测试m个context经过条件节点运行状态Running")
        public void testMNContextGetFlowCompletionByTraceIdStatusRunning() {
            when(flowTraceRepo.findTraceByIdList(anyList())).thenReturn(getFlowTrace());
            List<FlowContextPO> contextList = getConditionContextList();
            contextList.get(4).setStatus("NEW");
            contextList.get(5).setStatus("NEW");

            FlowContextPO po1 = FlowContextPO.builder().positionId("condi1").status("ARCHIVED").build();
            FlowContextPO po2 = FlowContextPO.builder().positionId("condi1").status("ARCHIVED").build();
            FlowContextPO po3 = FlowContextPO.builder().positionId("state2").status("ARCHIVED").build();
            FlowContextPO po4 = FlowContextPO.builder().positionId("state4").status("NEW").build();
            contextList.add(po1);
            contextList.add(po2);
            contextList.add(po3);
            contextList.add(po4);
            when(queryFlowContextPersistRepo.findByContextIdList(anyList())).thenReturn(contextList);
            String jsonData = getJsonData(FILE_PATH_PREFIX + "flows_calculate_completeness_with_condition_node.json");
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            when(flowDefinitionRepo.findByStreamId(anyString())).thenReturn(flowDefinition);
            Map<String, Map<String, Object>> result = flowContextsService.getFlowCompleteness(
                    Collections.singletonList("traceId"), false, getOperationContext());

            assertEquals(String.format(Locale.ROOT, "%.2f", (double) 13 / 21 * 100), result.get("percentage"));
            assertEquals("RUNNING", result.get("status"));
        }

        @Disabled
        @Test
        @DisplayName("测试重试自动任务成功")
        public void testRetryJoberSuccess() {
            FlowRetry flowRetry = new FlowRetry("toBatchId", "toBatch", LocalDateTime.now(), null, 0, 1);
            when(flowRetryRepo.filterByNextRetryTime(any())).thenReturn(Collections.singletonList(flowRetry));
            Lock lock = Mockito.mock(Lock.class);
            when(flowLocks.getDistributedLock("retry-toBatchId")).thenReturn(lock);
            when(lock.tryLock()).thenReturn(true);
            FlowContext<FlowData> context = new FlowContext("streamId", "rootId", null,
                    Collections.singleton("traceId"), "position");
            context.setStatus(RETRYABLE);
            context.toBatch("toBatchId");
            List<FlowContext<FlowData>> contexts = Collections.singletonList(context);
            when(flowContextRepo.getByToBatch("toBatchId")).thenReturn(contexts);
            FlowDefinition flowDefinition = Mockito.mock(FlowDefinition.class);
            when(flowDefinitionRepo.findByStreamId("streamId")).thenReturn(flowDefinition);
            From<FlowData> from = Mockito.mock(From.class);
            when(flowDefinition.convertToFlow(flowContextRepo, flowContextMessenger, flowLocks)).thenReturn(from);
            To<FlowData, Object> to = Mockito.mock(To.class);
            when(from.getSubscriber("position")).thenReturn(to);
            when(flowLocks.getDistributedLock(
                    flowLocks.streamNodeLockKey("streamId", "position", PROCESS.toString()))).thenReturn(lock);
            flowContextsService.retryJober();
            verify(lock, times(1)).tryLock();
            verify(lock, times(1)).lock();
            verify(flowContextRepo, times(1)).updateStatus(contexts, READY.toString(), "position");
            verify(flowRetryRepo, times(1)).updateRetryRecord(any());
            verify(lock, times(2)).unlock();
            assertNotNull(flowRetry.getLastRetryTime());
            assertEquals(1, flowRetry.getRetryCount());
        }

        private List<FlowContextPO> getContextList() {
            List<FlowContextPO> list = new ArrayList<>();
            FlowContextPO po1 = FlowContextPO.builder()
                    .contextId(UUIDUtil.uuid())
                    .positionId("start1")
                    .status("ARCHIVED")
                    .traceId("traceId")
                    .build();
            list.add(po1);

            FlowContextPO po2 = FlowContextPO.builder()
                    .contextId(UUIDUtil.uuid())
                    .positionId("state1")
                    .status("ARCHIVED")
                    .traceId("traceId")
                    .build();
            list.add(po2);

            FlowContextPO po3 = FlowContextPO.builder()
                    .contextId(UUIDUtil.uuid())
                    .positionId("ender1")
                    .status("ARCHIVED")
                    .traceId("traceId")
                    .build();
            list.add(po3);
            return list;
        }

        private List<FlowContextPO> getConditionContextList() {
            List<FlowContextPO> list = new ArrayList<>();
            FlowContextPO po1 = FlowContextPO.builder().positionId("start1").status("ARCHIVED").build();
            FlowContextPO po2 = FlowContextPO.builder().positionId("state1").status("ARCHIVED").build();
            FlowContextPO po3 = FlowContextPO.builder().positionId("condi1").status("ARCHIVED").build();
            FlowContextPO po4 = FlowContextPO.builder().positionId("state2").status("ARCHIVED").build();
            FlowContextPO po5 = FlowContextPO.builder().positionId("state3").status("ARCHIVED").build();
            FlowContextPO po6 = FlowContextPO.builder().positionId("ender1").status("ARCHIVED").build();
            list.add(po1);
            list.add(po2);
            list.add(po3);
            list.add(po4);
            list.add(po5);
            list.add(po6);
            return list;
        }

        private List<FlowTrace> getFlowTrace() {
            return Collections.singletonList(
                    FlowTrace.builder().streamId("streamId").contextPool(new HashSet<>()).build());
        }

        /**
         * 获取json
         *
         * @param fileName 文件名
         * @return json信息
         */
        protected String getJsonData(String fileName) {
            try (InputStream in = IoUtils.resource(FlowContextServiceTest.class.getClassLoader(), fileName)) {
                return new String(IoUtils.read(in), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new IllegalArgumentException();
            }
        }

        private OperationContext getOperationContext() {
            return OperationContext.custom().operator("yyk").operatorIp("0.0.0.1").tenantId("tianzhou").build();
        }
    }
}