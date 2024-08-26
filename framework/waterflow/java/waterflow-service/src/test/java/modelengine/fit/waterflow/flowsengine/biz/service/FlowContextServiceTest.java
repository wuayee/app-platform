/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service;

import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import com.huawei.fit.jane.task.util.OperationContext;

import com.alibaba.fastjson.JSON;

import modelengine.fit.waterflow.DatabaseBaseTest;
import modelengine.fit.waterflow.FlowsDataBaseTest;
import modelengine.fit.waterflow.common.utils.UUIDUtil;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowsErrorInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.QueryFlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * RestartContextSchedule对应测试类
 *
 * @author 杨祥宇
 * @since 2024/4/24
 */
@Disabled
class FlowContextServiceTest extends DatabaseBaseTest {
    private final Parser parser = new FlowParser(null);

    private FlowContextsService flowContextsService;

    private FlowTraceRepo traceRepo;

    private FlowContextPersistRepo contextPersistRepo;

    private DefaultFlowDefinitionRepo flowDefinitionRepo;

    private static FlowLocks locks = new FlowLocksMemo();

    private FlowContextPersistMessenger messenger = new FlowContextPersistMessenger(null);

    private static InvalidDistributedLockNotify notify = Mockito.mock(InvalidDistributedLockNotify.class);

    private static final TraceOwnerService traceOwnerService = new DefaultTraceOwnerService(locks, notify);

    private FlowTraceMapper traceMapper;

    private FlowContextMapper contextMapper;

    private FlowDefinitionMapper flowDefinitionMapper;

    private FitableUsageMapper fitableUsageMapper;

    private FlowRetryRepo retryRepo;

    @BeforeEach
    void setUp() {
        traceMapper = sqlSessionManager.getMapper(FlowTraceMapper.class);
        traceRepo = new DefaultFlowTraceRepo(traceMapper);
        contextMapper = sqlSessionManager.getMapper(FlowContextMapper.class);
        FlowRetryMapper retryMapper = sqlSessionManager.getMapper(FlowRetryMapper.class);
        retryRepo = new DefaultFlowRetryRepo(retryMapper);
        contextPersistRepo = new FlowContextPersistRepo(contextMapper, traceRepo, retryRepo, traceOwnerService, 10,
                false);
        flowDefinitionMapper = sqlSessionManager.getMapper(FlowDefinitionMapper.class);
        fitableUsageMapper = sqlSessionManager.getMapper(FitableUsageMapper.class);
        flowDefinitionRepo = new DefaultFlowDefinitionRepo(flowDefinitionMapper, fitableUsageMapper, parser);
        QueryFlowContextPersistRepo queryContextRepo = new QueryFlowContextPersistRepo(contextMapper);
        flowContextsService = new FlowContextsService(flowDefinitionRepo, contextPersistRepo, messenger,
                queryContextRepo, traceRepo, retryRepo, locks, traceOwnerService, new ArrayList<>(), null);
    }

    @Nested
    @DisplayName("FlowService功能测试")
    class RestartContextTest extends FlowsDataBaseTest {
        private String sql = "handler/flowContextService/saveData.sql";

        private String deleteSql = "handler/flowContextService/cleanData.sql";

        @AfterEach
        void down() {
            executeSqlInFile(deleteSql);
        }

        @Test
        @DisplayName("测试使用definitionId启动流程成功")
        void testStartFlowsByDefinitionIdSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setDefinitionId(UUIDUtil.uuid());
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);

            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");

            FlowOfferId flowOfferId = flowContextsService.startFlows(definition.getDefinitionId(), flowData, null);

            Assertions.assertTrue(traceOwnerService.isOwn(flowOfferId.getTraceId()));
        }

        @Test
        @DisplayName("测试使metaId和version启动流程成功")
        void testStartFlowsByStreamIdSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);

            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");

            FlowOfferId flowOfferId = flowContextsService.startFlows(definition.getMetaId(), definition.getVersion(),
                    JSON.toJSONString(flowData));

            Assertions.assertTrue(traceOwnerService.isOwn(flowOfferId.getTraceId()));
        }

        @Test
        @DisplayName("测试使metaId和version在某个trans下启动流程成功")
        void testStartFlowsByTransSuccess() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String transId = UUIDUtil.uuid();

            FlowOfferId flowOfferId = flowContextsService.startFlowsWithTrans(definition.getMetaId(),
                    definition.getVersion(), transId, JSON.toJSONString(flowData));

            Assertions.assertTrue(traceOwnerService.isOwn(flowOfferId.getTraceId()));
        }

        @Test
        @DisplayName("测试获取流程错误信息成功")
        void testGetFlowErrorInfoSuccess() {
            executeSqlInFile(sql);
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            String traceId = "10174dc2b03e4e15a7611ad3e66e736e";

            List<FlowsErrorInfo> flowErrorInfo = flowContextsService.getFlowErrorInfo(traceId);

            Assertions.assertEquals(1, flowErrorInfo.size());
            Assertions.assertEquals(10007501, flowErrorInfo.get(0).getContextErrorInfo().getErrorCode());
        }

        @Test
        @DisplayName("测试根据transId终止流程成功")
        void testTerminateFlowsSuccess() {
            executeSqlInFile(sql);
            String traceId = "11174dc2b03e4e15a7611ad3e66e736e";
            String transId = "1114ead3de8b4dd484c80f77c562b698";
            OperationContext operationContext = OperationContext.custom()
                    .tenantId("")
                    .operator("yxy")
                    .operatorIp("")
                    .sourcePlatform("")
                    .langage("")
                    .build();

            flowContextsService.terminateFlowsByTransId(transId, operationContext);

            FlowTrace flowTrace = traceRepo.find(traceId);
            List<FlowContext<FlowData>> contexts = contextPersistRepo.findByTraceId(traceId);
            Assertions.assertEquals(FlowTraceStatus.TERMINATE, flowTrace.getStatus());
            Assertions.assertEquals(1, contexts.size());
            Assertions.assertEquals(FlowNodeStatus.TERMINATE, contexts.get(0).getStatus());
        }

        @Disabled
        @Test
        @DisplayName("测试计算trace完成状态成功")
        void testCalculateFlowTraceStatusArchivedSuccess() {
            executeSqlInFile(sql);
            String traceId = "22274dc2b03e4e15a7611ad3e66e736e";
            String transId = "2224ead3de8b4dd484c80f77c562b698";
            traceOwnerService.own(traceId, transId);

            flowContextsService.calculateFlowTraceStatus();

            FlowTrace flowTrace = traceRepo.find(traceId);
            Assertions.assertEquals(FlowTraceStatus.ARCHIVED, flowTrace.getStatus());
        }

        @Test
        @DisplayName("测试计算流程百分比")
        void testGetFlowCompletenessSuccess() {
            executeSqlInFile(sql);
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition definition = parser.parse(jsonData);
            definition.setTenant("yxy");
            definition.setCreatedBy("yxy");
            flowDefinitionRepo.save(definition, jsonData);
            String traceId = "33374dc2b03e4e15a7611ad3e66e736e";

            Map<String, Map<String, Object>> flowCompleteness = flowContextsService.getFlowCompleteness(
                    Collections.singletonList(traceId), true, null);
            Map<String, Object> result = flowCompleteness.get(traceId);
            Assertions.assertEquals("50.00", result.get("percentage"));
            Assertions.assertEquals(FlowTraceStatus.ERROR.name(), result.get("status"));
            Assertions.assertEquals(definition.getFlowNode("state1").getName(),
                    ((List<String>) (result.get("errorNode"))).get(0));

            flowCompleteness = flowContextsService.getFlowCompleteness(Collections.singletonList(traceId), false, null);
            result = flowCompleteness.get(traceId);
            Assertions.assertEquals("50.00", result.get("percentage"));
            Assertions.assertEquals(FlowTraceStatus.ERROR.name(), result.get("status"));
            Assertions.assertEquals(definition.getFlowNode("state1").getName(),
                    ((List<String>) (result.get("errorNode"))).get(0));
        }

        @Test
        @DisplayName("测试根据streamId成功查询运行中、错误、已完成的context")
        void testFindContextStatusViewByStreamIdsSuccess() {
            executeSqlInFile(sql);
            String streamId = "executor-2.0.0";

            List<Map<String, Object>> result = flowContextsService.findContextStatusViewByStreamIds(
                    Collections.singletonList(streamId), null);

            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals(1, result.get(0).get("allContexts"));
            Assertions.assertEquals(1, result.get(0).get("runningContexts"));
            Assertions.assertEquals(1, result.get(0).get("errorContexts"));
        }

        @Override
        protected String getFilePathPrefix() {
            return "flows/executors/";
        }
    }
}