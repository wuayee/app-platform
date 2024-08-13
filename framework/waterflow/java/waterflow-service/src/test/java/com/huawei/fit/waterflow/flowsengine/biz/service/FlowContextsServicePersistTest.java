/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service;

import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitSingle;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ERROR;
import static org.mockito.Mockito.when;

import com.huawei.fit.waterflow.DatabaseBaseTest;
import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import com.huawei.fit.waterflow.flowsengine.persist.po.FlowRetryPO;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.plugin.Plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * {@link FlowContextsService} 对应测试类（数据库环境）
 *
 * @author 李哲峰
 * @since 2024/2/7
 */
@Disabled
public class FlowContextsServicePersistTest extends DatabaseBaseTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Plugin PLUGIN = Mockito.mock(Plugin.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    private static final FlowContextMapper FLOW_CONTEXT_MAPPER;

    private static final FlowTraceMapper FLOW_TRACE_MAPPER;

    private static final FlowTraceRepo TRACE_REPO;

    private static final FlowRetryMapper FLOW_RETRY_MAPPER;

    private static final FlowRetryRepo FLOW_RETRY_REPO;

    private static final FlowDefinitionMapper FLOW_DEFINITION_MAPPER;

    private static final FlowContextRepo<FlowData> REPO;

    private static final QueryFlowContextPersistRepo QUERY_REPO;

    private static final FlowDefinitionRepo DEFINITION_REPO;

    private static final FlowContextMessenger MESSENGER = new FlowContextPersistMessenger(PLUGIN);

    private static final FlowLocks LOCKS = new FlowLocksMemo();

    private static final FlowContextsService FLOW_CONTEXTS_SERVICE;

    private static final TraceOwnerService traceOwnerService;

    static {
        sqlSessionManager.startManagedSession(true);
        FLOW_CONTEXT_MAPPER = sqlSessionManager.getMapper(FlowContextMapper.class);
        FLOW_TRACE_MAPPER = sqlSessionManager.getMapper(FlowTraceMapper.class);
        TRACE_REPO = new DefaultFlowTraceRepo(FLOW_TRACE_MAPPER);
        FLOW_RETRY_MAPPER = sqlSessionManager.getMapper(FlowRetryMapper.class);
        FLOW_RETRY_REPO = new DefaultFlowRetryRepo(FLOW_RETRY_MAPPER);
        FLOW_DEFINITION_MAPPER = sqlSessionManager.getMapper(FlowDefinitionMapper.class);
        Integer defaultLimitation = 5;
        boolean useLimit = false;
        REPO = new FlowContextPersistRepo(FLOW_CONTEXT_MAPPER, TRACE_REPO, FLOW_RETRY_REPO, null, defaultLimitation,
                useLimit);
        QUERY_REPO = new QueryFlowContextPersistRepo(FLOW_CONTEXT_MAPPER);
        FitableUsageMapper fitableUsageMapper = sqlSessionManager.getMapper(FitableUsageMapper.class);
        DEFINITION_REPO = new DefaultFlowDefinitionRepo(FLOW_DEFINITION_MAPPER, fitableUsageMapper, PARSER);
        traceOwnerService = Mockito.mock(TraceOwnerService.class);
        FLOW_CONTEXTS_SERVICE = new FlowContextsService(DEFINITION_REPO, REPO, MESSENGER, QUERY_REPO, TRACE_REPO,
                FLOW_RETRY_REPO, LOCKS, traceOwnerService, null, null);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowDefinition/cleanData.sql");
        executeSqlInFile("handler/flowTrace/cleanData.sql");
        executeSqlInFile("handler/flowContext/cleanData.sql");
        executeSqlInFile("handler/flowRetry/cleanData.sql");
    }

    @Nested
    @DisplayName("测试重试任务合集")
    class RetryJoberTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("测试重试自动任务成功： 预设重试计划，然后重试自动任务遇到不可重试异常，流程终止")
        public void testRetryJoberSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            String streamId = flowDefinition.getStreamId();
            flowDefinition.setCreatedBy("lzf");
            flowDefinition.setTenant("tianzhou");
            DEFINITION_REPO.save(flowDefinition, jsonData);
            From<FlowData> flow = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            LocalDateTime startTime = LocalDateTime.parse("2024-02-01T11:18:01.011");
            FlowTrace flowTrace = FlowTrace.builder()
                    .streamId(streamId)
                    .operator("lzf")
                    .application("aipp")
                    .startNode("start1")
                    .endNode("ender1")
                    .startTime(startTime)
                    .status(FlowTraceStatus.RUNNING)
                    .contextPool(new HashSet<>())
                    .build();
            TRACE_REPO.save(flowTrace);
            String traceId = flowTrace.getId();
            FlowContext<FlowData> context = generateFlowContext(streamId, traceId, "state1", "toBatchId",
                    FlowNodeStatus.RETRYABLE);
            REPO.save(Collections.singletonList(context));
            TRACE_REPO.updateContextPool(Collections.singletonList(traceId),
                    Collections.singletonList(context.getId()));
            FlowRetry flowRetry = new FlowRetry("toBatchId", "toBatch", LocalDateTime.now(), null, 0, 1);
            FLOW_RETRY_REPO.save(Collections.singletonList(flowRetry));
            when(traceOwnerService.getTraces()).thenReturn(new ArrayList<>(context.getTraceId()));

            FLOW_CONTEXTS_SERVICE.retryJober();

            FlowRetryPO flowRetryPO = FLOW_RETRY_MAPPER.find("toBatchId");
            if (flowRetryPO != null) {
                Assertions.assertTrue(flowRetryPO.getNextRetryTime().isAfter(flowRetryPO.getLastRetryTime()));
                Assertions.assertEquals(1, flowRetryPO.getRetryCount());
                Assertions.assertEquals(2, flowRetryPO.getVersion());
            }
            waitSingle(contextSupplier(REPO, streamId, traceId, "state1", ERROR));
            Assertions.assertNull(FLOW_RETRY_MAPPER.find("toBatchId"));
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private FlowContext<FlowData> generateFlowContext(String streamId, String traceId, String position,
                String toBatch, FlowNodeStatus status) {
            HashMap<String, Object> businessData = new HashMap<>();
            businessData.put("application", "aipp");
            FlowData flowData = FlowData.builder()
                    .operator("lzf")
                    .startTime(LocalDateTime.now())
                    .businessData(businessData)
                    .contextData(new HashMap<>())
                    .build();
            FlowContext<FlowData> context = new FlowContext(streamId, "rootId", flowData,
                    Collections.singleton(traceId), position);
            context.setStatus(status);
            context.toBatch(toBatch);
            return context;
        }
    }
}
