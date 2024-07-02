/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service;

import static com.huawei.fit.waterflow.common.Constant.STREAM_ID_SEPARATOR;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.READY;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.ProcessType.PROCESS;
import static com.huawei.fit.waterflow.flowsengine.utils.FlowExecutors.getThreadPool;
import static com.huawei.fit.waterflow.flowsengine.utils.WaterFlows.getPublisher;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.waterflow.DatabaseBaseTest;
import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.common.utils.UUIDUtil;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.To;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowContextPO;
import com.huawei.fit.waterflow.flowsengine.utils.FlowExecutors;
import com.huawei.fit.waterflow.flowsengine.utils.PriorityThreadPool;
import com.huawei.fit.waterflow.flowsengine.utils.WaterFlows;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * {@link FlowContextsService}对应测试类
 *
 * @author 00693950
 * @since 2023/11/6
 */
class flowRetryTest extends DatabaseBaseTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    @Nested
    @DisplayName("测试流程实例service")
    class FlowContextServiceTest extends FlowsDataBaseTest {
        private static final String SQL_FILE = "handler/flowContext/saveData.sql";

        private static final String FILE_PATH_PREFIX = "flows/executors/";

        private FlowContextsService flowContextsService;

        private FlowContextRepo flowContextRepo;

        private FlowContextMessenger flowContextMessenger;

        private FlowLocks flowLocks;

        private FlowTraceRepo flowTraceRepo;

        private FlowRetryRepo flowRetryRepo;

        private QueryFlowContextPersistRepo queryFlowContextPersistRepo;

        private FlowDefinitionRepo flowDefinitionRepo;

        private TraceOwnerService traceOwnerService;

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
            traceOwnerService = Mockito.mock(TraceOwnerService.class);

            flowContextsService = new FlowContextsService(flowDefinitionRepo, flowContextRepo, flowContextMessenger,
                    queryFlowContextPersistRepo, flowTraceRepo, flowRetryRepo, flowLocks, traceOwnerService, null,
                    null);
        }

        @Test
        @DisplayName("测试重试自动任务成功")
        public void testRetryJoberSuccess() {
            FlowRetry flowRetry = new FlowRetry("toBatchId", "toBatch", LocalDateTime.now(), null, 0, 1);
            when(flowRetryRepo.filterByNextRetryTime(any())).thenReturn(Collections.singletonList(flowRetry));
            when(flowRetryRepo.updateRetryRecord(any())).thenReturn(1);
            Lock lock = Mockito.mock(Lock.class);
            when(flowLocks.getDistributedLock("retry-toBatchId")).thenReturn(lock);
            when(lock.tryLock()).thenReturn(true);
            FlowContext<FlowData> context = new FlowContext("streamId", "rootId", null,
                    Collections.singleton("traceId"), "position");
            context.setStatus(RETRYABLE);
            context.toBatch("toBatchId");
            List<FlowContext<FlowData>> contexts = Collections.singletonList(context);
            when(flowContextRepo.getByToBatch("toBatchId")).thenReturn(contexts);
            From<FlowData> from = Mockito.mock(From.class);
            To<FlowData, Object> to = Mockito.mock(To.class);
            when(from.getSubscriber("position")).thenReturn(to);
            when(flowLocks.getDistributedLock(
                    flowLocks.streamNodeLockKey("streamId", "position", PROCESS.toString()))).thenReturn(lock);
            when(traceOwnerService.getTraces()).thenReturn(new ArrayList<>(context.getTraceId()));
            MockedStatic<WaterFlows> waterFlows = Mockito.mockStatic(WaterFlows.class);
            waterFlows.when(() -> getPublisher("streamId")).thenReturn(from);

            MockedStatic<FlowExecutors> flowExecutors = Mockito.mockStatic(FlowExecutors.class);
            PriorityThreadPool threadPool = Mockito.mock(PriorityThreadPool.class);
            flowExecutors.when(() -> getThreadPool(
                    StringUtils.join(STREAM_ID_SEPARATOR, context.getStreamId(), context.getPosition()),
                    To.MAX_CONCURRENCY)).thenReturn(threadPool);
            doAnswer(invocation -> {
                PriorityThreadPool.PriorityTask task = invocation.getArgument(0);
                task.run();
                return null;
            }).when(threadPool).submit(any());

            doNothing().when(to).onProcess(anyList());
            flowContextsService.retryJober();
            verify(lock, times(1)).tryLock();
            verify(lock, times(1)).lock();
            verify(flowContextRepo, times(1)).updateStatus(contexts, READY.toString(), "position");
            verify(flowRetryRepo, times(1)).updateRetryRecord(any());
            verify(lock, times(2)).unlock();
            assertNotNull(flowRetry.getLastRetryTime());
            assertEquals(1, flowRetry.getRetryCount());
            // 释放waterFlows
            waterFlows.close();
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

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private OperationContext getOperationContext() {
            return OperationContext.custom().operator("yyk").operatorIp("0.0.0.1").tenantId("tianzhou").build();
        }
    }
}