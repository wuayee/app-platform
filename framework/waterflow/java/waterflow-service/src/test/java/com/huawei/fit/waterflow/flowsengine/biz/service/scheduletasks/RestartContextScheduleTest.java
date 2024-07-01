/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service.scheduletasks;

import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitSingle;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;

import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import com.huawei.fit.waterflow.DatabaseBaseTest;
import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.flowsengine.biz.service.DefaultTraceOwnerService;
import com.huawei.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * RestartContextSchedule对应测试类
 *
 * @author y00679285
 * @since 2024/4/24
 */
@Disabled
class RestartContextScheduleTest extends DatabaseBaseTest {
    private RestartContextSchedule restartContextSchedule;

    private FlowTraceRepo traceRepo;

    private FlowContextPersistRepo contextPersistRepo;

    private DefaultFlowDefinitionRepo flowDefinitionRepo;

    private FlowLocks locks = new FlowLocksMemo();

    private FlowContextPersistMessenger messenger = new FlowContextPersistMessenger(null);

    private TraceOwnerService traceOwnerService;

    private FlowTraceMapper traceMapper;

    private FlowContextMapper contextMapper;

    private FlowDefinitionMapper flowDefinitionMapper;

    private FitableUsageMapper fitableUsageMapper;

    private FlowRetryRepo retryRepo;

    private final Parser parser = new FlowParser(null);

    @BeforeEach
    void setUp() {
        traceMapper = sqlSessionManager.getMapper(FlowTraceMapper.class);
        traceRepo = new DefaultFlowTraceRepo(traceMapper);
        contextMapper = sqlSessionManager.getMapper(FlowContextMapper.class);
        InvalidDistributedLockNotify notify = Mockito.mock(InvalidDistributedLockNotify.class);
        traceOwnerService = new DefaultTraceOwnerService(locks, notify);
        FlowRetryMapper retryMapper = sqlSessionManager.getMapper(FlowRetryMapper.class);
        retryRepo = new DefaultFlowRetryRepo(retryMapper);
        contextPersistRepo = new FlowContextPersistRepo(contextMapper, traceRepo, retryRepo, traceOwnerService, 10,
                false);
        flowDefinitionMapper = sqlSessionManager.getMapper(FlowDefinitionMapper.class);
        fitableUsageMapper = sqlSessionManager.getMapper(FitableUsageMapper.class);
        flowDefinitionRepo = new DefaultFlowDefinitionRepo(flowDefinitionMapper, fitableUsageMapper, parser);
        restartContextSchedule = new RestartContextSchedule(traceRepo, contextPersistRepo, flowDefinitionRepo, locks,
                messenger, traceOwnerService, false);
    }

    @Nested
    @DisplayName("重启后context重试测试")
    class RestartContextTest extends FlowsDataBaseTest {
        private final String sql = "handler/flowRestart/saveData.sql";

        private final String deleteSql = "handler/flowRestart/cleanData.sql";

        @Override
        protected String getFilePathPrefix() {
            return "flows/executors/";
        }

        @Test
        @DisplayName("测试状态为new的context重试成功")
        void restartNewContextSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            executeSqlInFile(sql);
            String traceId = "10174dc2b03e4e15a7611ad3e66e736e";

            restartContextSchedule.restartInterruptContext();

            String contextId = "31b1ebbbde674babb22fafa56fe9062d";
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(contextPersistRepo, definition.getStreamId(), traceId, "state2", ARCHIVED));
            Assertions.assertEquals(FlowNodeStatus.ARCHIVED, contexts.get(0).getStatus());
            Assertions.assertEquals("state2", contexts.get(0).getPosition());
            Assertions.assertEquals(contextId, contexts.get(0).getId());
            executeSqlInFile(deleteSql);
        }

        @Test
        @DisplayName("测试状态为pending的context重试成功")
        void restartPendingContextSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            executeSqlInFile(sql);
            String traceId = "11174dc2b03e4e15a7611ad3e66e736e";

            restartContextSchedule.restartInterruptContext();

            String contextId = "1111ebbbde674babb22fafa56fe9062d";
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(contextPersistRepo, definition.getStreamId(), traceId, "state2", ARCHIVED));
            Assertions.assertEquals(FlowNodeStatus.ARCHIVED, contexts.get(0).getStatus());
            Assertions.assertEquals("state2", contexts.get(0).getPosition());
            Assertions.assertEquals(contextId, contexts.get(0).getId());
            executeSqlInFile(deleteSql);
        }

        @Test
        @DisplayName("测试状态为ready的context重试成功")
        void restartReadyContextSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            executeSqlInFile(sql);
            String traceId = "11174dc2b03e4e15a7611ad3e66e736e";

            restartContextSchedule.restartInterruptContext();

            String contextId = "1111ebbbde674babb22fafa56fe9062d";
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(contextPersistRepo, definition.getStreamId(), traceId, "state2", ARCHIVED));
            Assertions.assertEquals(FlowNodeStatus.ARCHIVED, contexts.get(0).getStatus());
            Assertions.assertEquals("state2", contexts.get(0).getPosition());
            Assertions.assertEquals(contextId, contexts.get(0).getId());
            executeSqlInFile(deleteSql);
        }
    }
}