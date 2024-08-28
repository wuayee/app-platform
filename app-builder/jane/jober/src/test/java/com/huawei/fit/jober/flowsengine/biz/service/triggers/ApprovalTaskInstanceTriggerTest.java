/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service.triggers;

import static com.huawei.fit.jober.FlowsTestUtil.waitSingle;
import static com.huawei.fit.jober.common.Constant.OPERATOR_KEY;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType.END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.FlowsDataBaseTest;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.utils.SleepUtil;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;

import modelengine.fit.waterflow.flowsengine.biz.service.DefaultTraceOwnerService;
import modelengine.fit.waterflow.flowsengine.biz.service.FlowContextsService;
import modelengine.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.DefaultFlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.repo.FlowDefinitionRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FitableUsageMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowDefinitionMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import modelengine.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程实例恢复执行fitable类测试
 * 简述
 *
 * @author 高诗意
 * @since 2023/09/28
 */
@Disabled
public class ApprovalTaskInstanceTriggerTest extends DatabaseBaseTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Plugin PLUGIN = Mockito.mock(Plugin.class);

    private static final FitRuntime FIT_RUNTIME = Mockito.mock(FitRuntime.class);

    private static final EventPublisher EVENT_PUBLISHER = Mockito.mock(EventPublisher.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    private static final FlowContextMapper FLOW_CONTEXT_MAPPER;

    private static final FlowTraceMapper FLOW_TRACE_MAPPER;

    private static final FlowTraceRepo TRACE_REPO;

    private static final FlowRetryMapper FLOW_RETRY_MAPPER;

    private static final FlowRetryRepo FLOW_RETRY_REPO;

    private static final FlowDefinitionMapper FLOW_DEFINITION_MAPPER;

    private static final FlowContextRepo<FlowData> FLOW_CONTEXT_REPO;

    private static final FlowDefinitionRepo DEFINITION_REPO;

    private static final FlowContextMessenger MESSENGER = new FlowContextPersistMessenger(PLUGIN);

    private static final FlowLocks LOCKS = new FlowLocksMemo();

    private static final ApprovalTaskInstanceTrigger TRIGGER;

    private static final FlowContextsService FLOW_CONTEXTS_SERVICE;

    private static final FitableUsageMapper FITABLE_USAGE_MAPPER;

    private static final TraceOwnerService TRACE_OWNER_SERVICE;

    static {
        sqlSessionManager.startManagedSession(true);
        FLOW_CONTEXT_MAPPER = sqlSessionManager.getMapper(FlowContextMapper.class);
        FLOW_TRACE_MAPPER = sqlSessionManager.getMapper(FlowTraceMapper.class);
        TRACE_REPO = new DefaultFlowTraceRepo(FLOW_TRACE_MAPPER);
        FLOW_RETRY_MAPPER = sqlSessionManager.getMapper(FlowRetryMapper.class);
        FLOW_RETRY_REPO = new DefaultFlowRetryRepo(FLOW_RETRY_MAPPER);
        FLOW_DEFINITION_MAPPER = sqlSessionManager.getMapper(FlowDefinitionMapper.class);
        InvalidDistributedLockNotify invalidDistributedLockNotify = Mockito.mock(InvalidDistributedLockNotify.class);
        TRACE_OWNER_SERVICE = new DefaultTraceOwnerService(LOCKS, invalidDistributedLockNotify);
        Integer defaultLimitation = 5;
        boolean useLimit = false;
        FLOW_CONTEXT_REPO = new FlowContextPersistRepo(FLOW_CONTEXT_MAPPER, TRACE_REPO, FLOW_RETRY_REPO,
                TRACE_OWNER_SERVICE, defaultLimitation, useLimit);
        FITABLE_USAGE_MAPPER = sqlSessionManager.getMapper(FitableUsageMapper.class);
        DEFINITION_REPO = new DefaultFlowDefinitionRepo(FLOW_DEFINITION_MAPPER, FITABLE_USAGE_MAPPER, PARSER);
        FLOW_CONTEXTS_SERVICE = new FlowContextsService(DEFINITION_REPO, FLOW_CONTEXT_REPO, MESSENGER, null, TRACE_REPO,
                FLOW_RETRY_REPO, LOCKS, TRACE_OWNER_SERVICE, null, null);
        TRIGGER = new ApprovalTaskInstanceTrigger(FLOW_CONTEXT_REPO, DEFINITION_REPO, FLOW_CONTEXTS_SERVICE);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowDefinition/cleanData.sql");
    }

    @Nested
    @DisplayName("流程实例手动流转发送事件触发恢复fitable接口的持久化测试集合")
    class FlowableManualOperationTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例手动流转condition节点true分支的场景持久化")
        @Disabled
        void testFlowsManualExecutorWithConditionNodeTrueBranch() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setCreatedBy("yxy");
            flowDefinition.setTenant("tianzhou");
            DEFINITION_REPO.save(flowDefinition, jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(FLOW_CONTEXT_REPO, MESSENGER, LOCKS);
            String metaId = "event1"; // 来自json文件中的配置
            String streamId = flowDefinition.getStreamId();

            when(PLUGIN.runtime()).thenReturn(FIT_RUNTIME);
            when(FIT_RUNTIME.publisherOfEvents()).thenReturn(EVENT_PUBLISHER);
            doAnswer(invocation -> null).when(EVENT_PUBLISHER).publishEvent(any());

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(FLOW_CONTEXT_REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> allContexts = this.getContextsByTraceWrapper(FLOW_CONTEXT_REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, allContexts);

            OperationContext context = new OperationContext("", OPERATOR_KEY, null, null, null);
            TRIGGER.update(null, getInstance("true", contexts.get(0).getId()), getChangedValue("true"), context);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> resumeContextList = waitSingle(
                    contextSupplier(FLOW_CONTEXT_REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> resumeAllContexts = this.getContextsByTraceWrapper(FLOW_CONTEXT_REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(resumeContextList, resumeAllContexts, 5,
                    "echo: success");
            assertEquals("echo: gsy", resumeContextList.get(0).getData().getBusinessData().get("owner1"));
        }

        @Test
        @Disabled
        @DisplayName("流程实例手动流转condition节点false分支的场景持久化")
        void testFlowsManualExecutorWithConditionNodeFalseBranch() {
            String jsonGraph = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonGraph);
            flowDefinition.setCreatedBy("yxy");
            flowDefinition.setTenant("tianzhou");
            DEFINITION_REPO.save(flowDefinition, jsonGraph);
            FlowData data = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(FLOW_CONTEXT_REPO, MESSENGER, LOCKS);
            String metaId = "event1"; // 来自json文件中的配置
            String streamId = flowDefinition.getStreamId();

            when(PLUGIN.runtime()).thenReturn(FIT_RUNTIME);
            when(FIT_RUNTIME.publisherOfEvents()).thenReturn(EVENT_PUBLISHER);
            doAnswer(invocation -> {
                return null;
            }).when(EVENT_PUBLISHER).publishEvent(any());

            // TODO 手动任务连续执行后一个会阻塞，添加等待时间规避，后续问题待定位
            SleepUtil.sleep(5000);
            String traceId = from.offer(data).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(FLOW_CONTEXT_REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> allContexts = this.getContextsByTraceWrapper(FLOW_CONTEXT_REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, allContexts);

            OperationContext context = new OperationContext("", OPERATOR_KEY, null, null, null);
            TRIGGER.update(null, getInstance("false", contexts.get(0).getId()), getChangedValue("false"), context);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> resumeContexts = waitSingle(
                    contextSupplier(FLOW_CONTEXT_REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> resumeAllContexts = this.getContextsByTraceWrapper(FLOW_CONTEXT_REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(resumeContexts, resumeAllContexts, 4,
                    "success");
            assertEquals("gsy", resumeContexts.get(0).getData().getBusinessData().get("owner1"));
        }

        @Test
        @DisplayName("metaId+version或名称+version已存在，创建流程定义失败")
        void testCreateFlowsFailedWhenFlowAlreadyExist() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setCreatedBy("yxy");
            flowDefinition.setTenant("tianzhou");
            DEFINITION_REPO.save(flowDefinition, jsonData);

            // repeat create
            Assertions.assertThrows(JobberException.class, () -> DEFINITION_REPO.save(flowDefinition, jsonData));

            // same name and version
            flowDefinition.setMetaId("executor_new");
            Assertions.assertThrows(JobberException.class, () -> DEFINITION_REPO.save(flowDefinition, jsonData));
        }

        @Test
        @DisplayName("流程实例手动任务不包含status字段时跳过本次恢复")
        void testFlowsManualExecutorWithSkip() {
            OperationContext context = new OperationContext("", OPERATOR_KEY, null, null, null);
            TRIGGER.update(null, getInstance("true", "contextId"), new HashMap<>(), context);
        }

        private Instance getInstance(String result, String flowContextId) {
            Instance instance = new Instance();
            Map<String, String> info = new HashMap<>();
            info.put("id", flowContextId);
            info.put("status", result);
            info.put("owner", "gsy");
            instance.setInfo(info);
            return instance;
        }

        private Map<String, Object> getChangedValue(String result) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", result);
            map.put("owner", "yxy");
            return map;
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }
}