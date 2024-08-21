/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.context;

import static com.huawei.fit.jober.common.ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitEmpty;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitFortyMillis;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitMillis;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitSingle;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.FlowsTestUtil.waitSize;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ERROR;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.RETRYABLE;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType.END;
import static com.huawei.fit.waterflow.flowsengine.utils.WaterFlows.getPublisher;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.data.repository.exception.CapacityOverflowException;
import com.huawei.fit.jane.task.gateway.InvalidDistributedLockNotify;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.DatabaseBaseTest;
import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.common.utils.UUIDUtil;
import com.huawei.fit.waterflow.flowsengine.biz.service.DefaultTraceOwnerService;
import com.huawei.fit.waterflow.flowsengine.domain.flows.Activities.Start;
import com.huawei.fit.waterflow.flowsengine.domain.flows.Flows;
import com.huawei.fit.waterflow.flowsengine.domain.flows.Flows.ProcessFlow;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextPersistRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.DefaultFlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.DefaultFlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowtrace.FlowTraceRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowContextMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowRetryMapper;
import com.huawei.fit.waterflow.flowsengine.persist.mapper.FlowTraceMapper;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.event.EventPublisher;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.ObjectUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * flow context持久化测试
 *
 * @author 杨祥宇
 * @since 2023/8/23
 */
@DisplayName("流程实例在数据库中运行测试集合")
@Disabled
public class FlowContextPersistTest extends DatabaseBaseTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Router ROUTER = Mockito.mock(Router.class);

    private static final Invoker INVOKER = Mockito.mock(Invoker.class);

    private static final Plugin PLUGIN = Mockito.mock(Plugin.class);

    private static final FitRuntime FIT_RUNTIME = Mockito.mock(FitRuntime.class);

    private static final EventPublisher EVENT_PUBLISHER = Mockito.mock(EventPublisher.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    private static final FlowContextMapper FLOW_CONTEXT_MAPPER;

    private static final FlowTraceMapper FLOW_TRACE_MAPPER;

    private static final FlowTraceRepo FLOW_TRACE_REPO;

    private static final FlowRetryMapper FLOW_RETRY_MAPPER;

    private static final FlowRetryRepo FLOW_RETRY_REPO;

    private static final FlowContextRepo<FlowData> REPO;

    private static final FlowContextMessenger MEMO_MESSENGER = new FlowContextMemoMessenger();

    private static final FlowContextMessenger PERSIST_MESSENGER = new FlowContextPersistMessenger(PLUGIN);

    private static final FlowLocks LOCKS = new FlowLocksMemo();

    static {
        sqlSessionManager.startManagedSession(true);
        FLOW_CONTEXT_MAPPER = sqlSessionManager.getMapper(FlowContextMapper.class);
        FLOW_TRACE_MAPPER = sqlSessionManager.getMapper(FlowTraceMapper.class);
        FLOW_TRACE_REPO = new DefaultFlowTraceRepo(FLOW_TRACE_MAPPER);
        FLOW_RETRY_MAPPER = sqlSessionManager.getMapper(FlowRetryMapper.class);
        FLOW_RETRY_REPO = new DefaultFlowRetryRepo(FLOW_RETRY_MAPPER);
        InvalidDistributedLockNotify invalidDistributedLockNotify = new InvalidDistributedLockNotify() {
            @Override
            public void subscribe(Consumer<Lock> consumer) {
            }

            @Override
            public void notify(Lock invalidLock) {
            }
        };
        Integer defaultLimitation = 5;
        boolean useLimit = false;
        REPO = new FlowContextPersistRepo(FLOW_CONTEXT_MAPPER, FLOW_TRACE_REPO, FLOW_RETRY_REPO,
                new DefaultTraceOwnerService(LOCKS, invalidDistributedLockNotify), defaultLimitation,
                useLimit);
    }

    @Nested
    @DisplayName("测试stream flowContext持久化")
    class StreamFlowContextPersistTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/convertors/";

        @Test
        @DisplayName("测试只包含map的context持久化成功")
        void testFlowContextPersistWithMapSuccess() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");

            Flows.<FlowData>create(REPO, MEMO_MESSENGER, LOCKS)
                    .map(i -> i)
                    .close(r -> result.add(r.get().getData()))
                    .offer(data);
            waitSingle(() -> result);
            assertEquals(data.getBusinessData().get("url"), result.get(0).getBusinessData().get("url"));
        }

        @Test
        @DisplayName("测试m->n持久化成功")
        void testFlowContextPersistWithProduceSuccess() {
            List<FlowData> result = new ArrayList<>();
            FlowData data2 = genFlowData("name", "yxy");
            FlowData data3 = genFlowData("result", "success");
            ProcessFlow<FlowData> flow = Flows.<FlowData>create(REPO, MEMO_MESSENGER, LOCKS).produce(i -> {
                i.add(data2);
                return i;
            }).produce(i -> {
                i.add(data3);
                return i;
            }).close(r -> result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList())));

            FlowData data = genFlowData("url", "www.123.com");
            flow.offer(data);
            waitSize(() -> result, 3);
            Assertions.assertEquals(3, result.size());
            assertEquals(data.getBusinessData().get("application"),
                    result.get(0).getBusinessData().get("application"));
            assertEquals(data2.getBusinessData().get("name"), result.get(1).getBusinessData().get("name"));
            assertEquals(data3.getBusinessData().get("result"),
                    result.get(2).getBusinessData().get("result"));

            FlowData data1 = genFlowData("applyService", "fitable");
            flow.offer(data1);
            waitSize(() -> result, 6);
            Assertions.assertEquals(6, result.size());
        }

        @Test
        @DisplayName("测试带有block流程实例持久化成功")
        void testFlowContextPersistWithBlockSuccess() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");
            FlowData data1 = genFlowData("applyService", "fitable");
            FlowData data2 = genFlowData("name", "yxy");
            FlowData data3 = genFlowData("result", "success");

            FlowData[] inputs = {data, data1};
            Blocks.FilterBlock<FlowData> block = new Blocks.FilterBlock<>();

            Start<FlowData, FlowData, ProcessFlow<FlowData>> start = Flows.create(REPO, MEMO_MESSENGER, LOCKS);
            ProcessFlow<FlowData> flow = start.produce(i -> {
                i.add(data2);
                return i;
            }).block(block).produce(i -> {
                i.add(data3);
                return i;
            }).close(r -> result.addAll(r.getAll().stream().map(FlowContext::getData).collect(Collectors.toList())));

            String traceId = flow.offer(inputs).getTraceId();
            List<FlowContext<FlowData>> contexts = waitSize(
                    contextSupplier(REPO, traceId, start.getSubscriptionsId().get(0), PENDING), 2);
            Assertions.assertEquals(0, result.size());

            waitMillis(Collections::emptyList, 100);
            String toBatch = UUIDUtil.uuid();
            contexts.forEach(c -> c.toBatch(toBatch));
            REPO.updateFlowData(contexts);
            block.process(contexts);
            waitSize(() -> result, 4);
            Assertions.assertEquals(4, result.size());
        }

        @Disabled
        @Test
        @DisplayName("测试带有condition节点流程实例持久化成功")
        void testFlowContextPersistWithCondition() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");
            FlowData data1 = genFlowData("applyService", "fitable");

            ProcessFlow<FlowData> flow = Flows.<FlowData>create(REPO, MEMO_MESSENGER, LOCKS)
                    .conditions()
                    .match(i -> i.getData().getBusinessData().equals(data.getBusinessData()))
                    .just(i -> i.getBusinessData().put("url", "success"))
                    .match(i -> i.getData().getBusinessData().equals(data1.getBusinessData()))
                    .just(i -> i.getBusinessData().put("applyService", "success"))
                    .close(r -> result.add(r.get().getData()));

            flow.offer(data);
            waitSingle(() -> result);
            FlowData data2 = genFlowData("url", "success");
            assertEquals(data2.getBusinessData(), result.get(0).getBusinessData());

            result.clear();
            flow.offer(data1);
            waitSingle(() -> result);
            FlowData data3 = genFlowData("applyService", "success");
            assertEquals(data3.getBusinessData(), result.get(0).getBusinessData());
        }

        @Test
        @DisplayName("测试一个节点不同实例context查找某一个实例context成功")
        void testFlowContextPersistWithMoreThanOneContextInNode() {
            List<FlowData> result = new ArrayList<>();
            FlowData data = genFlowData("url", "www.123.com");

            Blocks.FilterBlock<FlowData> block = new Blocks.FilterBlock<>();

            Start<FlowData, FlowData, ProcessFlow<FlowData>> start = Flows.create(REPO, MEMO_MESSENGER, LOCKS);
            ProcessFlow<FlowData> flow = start.produce(i -> {
                result.clear();
                result.addAll(i);
                return i;
            }).block(block).produce(i -> i).close();

            String traceId1 = flow.offer(data).getTraceId();
            List<FlowContext<FlowData>> contexts1 = waitSingle(
                    contextSupplier(REPO, traceId1, start.getSubscriptionsId().get(0), PENDING));
            Assertions.assertEquals(0, result.size());

            FlowData data1 = genFlowData("applyService", "fitable");
            String traceId2 = flow.offer(data1).getTraceId();
            List<FlowContext<FlowData>> contexts2 = waitSingle(
                    contextSupplier(REPO, traceId2, start.getSubscriptionsId().get(0), PENDING));
            Assertions.assertEquals(0, result.size());

            waitMillis(Collections::emptyList, 100);
            String toBatch1 = UUIDUtil.uuid();
            contexts1.forEach(c -> c.toBatch(toBatch1));
            REPO.updateFlowData(contexts1);
            block.process(contexts1);
            waitSingle(() -> result);
            Assertions.assertEquals(1, result.size());
            assertEquals(data.getBusinessData().get("application"),
                    result.get(0).getBusinessData().get("application"));
            assertEquals(data.getBusinessData().get("url"), result.get(0).getBusinessData().get("url"));
            result.clear();

            waitMillis(Collections::emptyList, 100);
            String toBatch2 = UUIDUtil.uuid();
            contexts2.forEach(c -> c.toBatch(toBatch2));
            REPO.updateFlowData(contexts2);
            block.process(contexts2);
            waitSingle(() -> result);
            Assertions.assertEquals(1, result.size());
            assertEquals(data1.getBusinessData().get("application"),
                    result.get(0).getBusinessData().get("application"));
            assertEquals(data1.getBusinessData().get("url"), result.get(0).getBusinessData().get("url"));
        }

        private FlowData genFlowData(String key, String value) {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("application", "tianzhou");
            businessData.put(key, value);
            return FlowData.builder()
                    .operator("yxy")
                    .startTime(LocalDateTime.now())
                    .businessData(businessData)
                    .contextData(new HashMap<>())
                    .build();
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例自动流转1TO1在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点的场景")
        void testFlowsExecutorWithOnlyStateNode1To1() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithOnlyStateNode1To1(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点第一个节点错误的场景")
        void testFlowsExecutorStateNodeWithErrorForFirstNode1To1() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setMetaId(stateError1);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String metaId = "state1";
            FlowNode flowNode = flowDefinition.getFlowNode(metaId);
            FlowJober flowJoberMocker = mock(FlowJober.class);
            flowNode.setJober(flowJoberMocker);
            JobberParamException expectException = new JobberParamException(INPUT_PARAM_IS_INVALID, "xxx");
            when(flowJoberMocker.execute(any())).thenThrow(expectException);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 2, expectException);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点第二个节点错误的场景")
        void testFlowsExecutorStateNodeWithErrorForSecondNode1To1() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setMetaId(stateError2);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String metaId = "state2";
            FlowNode flowNode = flowDefinition.getFlowNode(metaId);
            FlowJober flowJoberMocker = mock(FlowJober.class);
            flowNode.setJober(flowJoberMocker);
            JobberParamException expectException = new JobberParamException(INPUT_PARAM_IS_INVALID, "xxx");
            when(flowJoberMocker.execute(any())).thenThrow(expectException);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 3, expectException);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1通过场景")
        void testFlowsExecutorWithConditionNodeFirstBranchTrue() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithConditionNodeFirstBranchTrue(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1驳回场景")
        void testFlowsExecutorWithConditionNodeFirstBranchFalse() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            Map<String, Object> businessData = flowsExecutorWithConditionNodeFirstBranchTrue();
            businessData.put("cmc", new HashMap<String, Boolean>() {
                {
                    put("approved", false);
                }
            });
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithConditionNodeFirstFalseBranch(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支2驳回场景")
        void testFlowsExecutorWithConditionNodeSecondBranchFalse() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            Map<String, Object> businessData = flowsExecutorWithConditionNodeFirstBranchTrue();
            businessData.put("committer", new HashMap<String, Boolean>() {
                {
                    put("approved", false);
                }
            });
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithConditionNodeSecondFalseBranch(flowData, contexts, all);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点异常场景")
        void testFlowsExecutorConditionNodeWithError() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setMetaId(conditionError1);
            FlowData flowData = getFlowData(flowsExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            String metaId = "state1";
            FlowNode flowNode = flowDefinition.getFlowNode(metaId);
            FlowJober flowJoberMocker = mock(FlowJober.class);
            flowNode.setJober(flowJoberMocker);
            JobberParamException expectException = new JobberParamException(INPUT_PARAM_IS_INVALID, "xxx");
            when(flowJoberMocker.execute(any())).thenThrow(expectException);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 3, expectException);
        }
    }

    @Nested
    @DisplayName("流程实例手动流转1TO1在数据库持久化的场景测试集合")
    class FlowManualExecutorInPersistTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1通过的场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchTrue() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, all);

            waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            From<FlowData> resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            Blocks.Block<FlowData> block = resumeFrom.getBlock(resumeContext.getPosition());

            resumeContext.getData().getBusinessData().put("status", "true");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> resumeContexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> resumeAll = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(resumeContexts, resumeAll, 5,
                    "echo: success");
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1驳回的场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchFalse() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, all);

            waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            From<FlowData> resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            Blocks.Block<FlowData> block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", false);
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> resumeContexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> resumeAll = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueAfterBlock(resumeContexts, resumeAll, 4,
                    "success");
        }

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务转审的场景测试")
        void testFlowsManualExecutorWithConditionNodeCircle() {
            String jsonData = getJsonData(getFilePath("创建联调分支扩展转审场景.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeCircle(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置
            when(PLUGIN.runtime()).thenReturn(FIT_RUNTIME);
            when(FIT_RUNTIME.publisherOfEvents()).thenReturn(EVENT_PUBLISHER);
            doAnswer(invocation -> null).when(EVENT_PUBLISHER).publishEvent(any());

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 2, PENDING);
            assertNull(contexts.get(0).getToBatch());

            waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            From<FlowData> resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            Blocks.Block<FlowData> block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "transferred");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            metaId = "event5";
            contexts = waitSingle(contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 4, PENDING);
            assertNull(contexts.get(0).getToBatch());

            waitMillis(Collections::emptyList, 100);
            resumeContext = contexts.get(0);
            resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "approved");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Collections.singletonList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            contexts = waitSingle(contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 7, ARCHIVED);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例自动流转1TO1JoberWithFitable节点任务在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistWithJoberIncludeFitableTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1只有state节点的执行GeneralJober任务场景测试")
        void testFlowsExecuteGeneralJober() {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            executeJober(flowData);
            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteGeneralJober(contexts, all);
        }

        @Test
        @DisplayName("测试实例自动流转state节点执行GeneralJober执行失败抛出异常")
        void testFlowsExecuteGeneralJoberError() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            when(mockInvoker.invoke(anyList())).thenThrow(new JobberException(FLOW_EXECUTE_FITABLE_TASK_FAILED));
            when(mockInvoker.invoke(any(), anyList(), anyString())).thenReturn(null);

            String traceId = from.offer(flowData).getTraceId();

            String metaId = "state1";
            List<FlowContext<FlowData>> contexts = waitSingle(contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecuteGeneralJoberError(mockInvoker, flowDefinition, metaId, contexts, all);
        }

        @Test
        @DisplayName("测试实例自动流转state节点执行GeneralJober执行失败抛出可重试异常")
        void testFlowsExecuteGeneralJoberWithRetryableException() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            CapacityOverflowException exception = new CapacityOverflowException("test");
            when(mockInvoker.invoke(anyList())).thenThrow(exception);
            when(mockInvoker.invoke(any(), anyList(), anyString())).thenReturn(null);

            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "lzf");
            String traceId = from.offer(flowData).getTraceId();

            String metaId = "state1";
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, RETRYABLE));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecuteGeneralJoberWithRetryableException(from, metaId, contexts, all, FLOW_RETRY_MAPPER);
            executeSqlInFile("handler/flowRetry/cleanData.sql");
        }

        @Test
        @DisplayName("流程实例1到1只有state节点执行GeneralJober时动态修改fitable调用")
        void testFlowsExecuteGeneralJoberAndModifyFitables() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);

            Map<String, Object> businessData = flowsExecutorWithOnlyStateNode1To1();
            businessData.put("state1", JSONObject.parseObject(
                    "{\"name\":\"通知\",\"jober\":{\"type\":\"general_jober\",\"fitables\""
                            + ":[\"423629ecfbe24b3987dd3fa66ca74e8e\"]}}"));
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MEMO_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            executeJober(flowData);
            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> allContexts = waitSize(() -> this.getContextsByTraceWrapper(REPO, traceId), 3);

            JSONObject jsonObject = ObjectUtils.cast(allContexts.get(0).getData().getBusinessData().get("state1"));
            JSONObject jober = ObjectUtils.cast(jsonObject.get("jober"));
            JSONArray newFitables = ObjectUtils.cast(jober.get("fitables"));
            Set<String> oldFitables = flowDefinition.getFlowNode("state1").getJober().getFitables();

            assertEquals("创建分支实现", oldFitables.stream().findFirst().get());
            assertEquals("423629ecfbe24b3987dd3fa66ca74e8e", newFitables.getString(0));
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }

        private void executeJober(FlowData flowData) {
            List<Map<String, Object>> outputs = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put("businessData", flowData.getBusinessData());
            outputs.add(data);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(outputs);
        }
    }

    @Nested
    @DisplayName("流程实例流转M_TO_N在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistMToNTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer一个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferOneData() {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "gsy"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "yyk"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(false), "yxy"));
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = new ArrayList<>();
            list.forEach(flowData -> {
                Map<String, Object> data = new HashMap<>();
                data.put("businessData", flowData.getBusinessData());
                data.put("passData", new HashMap<>());
                outputs.add(data);
            });
            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            when(mockInvoker.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0)).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSize(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED), 3);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNForOfferOneData(contexts, all);
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer三个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferMultiData() {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            ArrayList<FlowData> flowData = new ArrayList<>();
            flowData.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "gsy"));
            flowData.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "yyk"));
            flowData.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(false), "yxy"));
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = new ArrayList<>();
            flowData.forEach(flowDatum -> {
                Map<String, Object> data = new HashMap<>();
                data.put("businessData", flowDatum.getBusinessData());
                data.put("passData", new HashMap<>());
                outputs.add(data);
            });
            Router mockRouter = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(mockRouter);
            Invoker mockInvoker = Mockito.mock(Invoker.class);
            when(mockRouter.route(any())).thenReturn(mockInvoker);
            when(mockInvoker.communicationType(any())).thenReturn(mockInvoker);
            when(mockInvoker.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(flowData.toArray(new FlowData[0])).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSize(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED), 3);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNForOfferMultiData(contexts, all);
        }

        @Disabled
        @Test
        @DisplayName("流程实例m到n包含condition节点包含前后过滤器的场景测试")
        void testFlowsExecuteFilterFromMToN() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData1 = getFlowData(flowsExecuteFilterFromMToN(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String eventMetaId = "event1"; // 来自json文件中的配置
            String stateMetaId = "state1"; // 来自json文件中的配置
            when(PLUGIN.runtime()).thenReturn(FIT_RUNTIME);
            when(FIT_RUNTIME.publisherOfEvents()).thenReturn(EVENT_PUBLISHER);
            doAnswer(invocation -> null).when(EVENT_PUBLISHER).publishEvent(any());

            String traceId1 = from.offer(flowData1).getTraceId();
            List<FlowContext<FlowData>> contexts1 = waitSingle(
                    contextSupplier(REPO, streamId, traceId1, eventMetaId, PENDING));
            assertEquals(1, contexts1.size());

            checkContext(contexts1, true);
            contexts1 = waitFortyMillis(contextSupplier(REPO, streamId, traceId1, eventMetaId, PENDING));
            assertEquals(1, contexts1.size());
            verify(EVENT_PUBLISHER, times(0)).publishEvent(any());

            FlowData flowData2 = getFlowData(flowsExecuteFilterFromMToN(), "yyk");
            String traceId2 = from.offer(flowData2).getTraceId();
            List<FlowContext<FlowData>> contexts2 = waitSingle(
                    contextSupplier(REPO, streamId, traceId2, eventMetaId, PENDING));
            assertEquals(1, contexts2.size());

            checkContext(contexts2, true);
            contexts2 = waitFortyMillis(contextSupplier(REPO, streamId, traceId2, eventMetaId, PENDING));
            assertEquals(1, contexts2.size());
            verify(EVENT_PUBLISHER, times(1)).publishEvent(any());

            FlowData flowData3 = getFlowData(flowsExecuteFilterFromMToN(), "yxy");
            String traceId3 = from.offer(flowData3).getTraceId();
            List<FlowContext<FlowData>> contexts3 = waitSingle(
                    contextSupplier(REPO, streamId, traceId3, eventMetaId, PENDING));
            assertEquals(1, contexts3.size());

            checkContext(contexts3, false);
            contexts3 = waitEmpty(contextSupplier(REPO, streamId, traceId3, eventMetaId, PENDING));
            assertEquals(0, contexts3.size());
            verify(EVENT_PUBLISHER, times(1)).publishEvent(any());

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> endContexts = waitSize(
                    contextSupplier(REPO, streamId, traceId1, flowNode.getMetaId(), ARCHIVED), 3);
            assertFlowsExecuteFilterFromMToN(REPO, traceId1, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId2, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId3, endContexts);
        }

        private void checkContext(List<FlowContext<FlowData>> contexts1, boolean isApproved) {
            waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext1 = contexts1.get(0);
            From<FlowData> resumeFrom1 = (From<FlowData>) getPublisher(resumeContext1.getStreamId());
            Blocks.Block<FlowData> block1 = resumeFrom1.getBlock(resumeContext1.getPosition());
            String toBatch1 = UUIDUtil.uuid();
            contexts1.forEach(c -> {
                c.getData().getBusinessData().put("approve", new HashMap<String, Boolean>() {
                    {
                        put("status", isApproved);
                    }
                });
                c.toBatch(toBatch1);
            });
            REPO.update(contexts1);
            block1.process(contexts1);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例流转M_TO_N添加过滤器后按批次流转在数据库持久化的场景测试集合")
    class FlowAutoExecutorInPersistMToNWithFilterBatchSizeOne extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例m到n最小SIZE过滤器为1单线程offer的场景测试")
        void testFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread() {
            String jsonData = getJsonData(
                    getFilePath("flows_auto_general_jober_state_node_m_to_n_with_filter_batch_size_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            int m = 5;
            for (int j = 0; j < m; j++) {
                list.add(getFlowData(flowsExecuteFilterFromMToN(), "gsy"));
            }
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, PERSIST_MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = list.stream().map(f -> {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> businessData = f.getBusinessData();
                result.put("businessData", businessData);
                return result;
            }).collect(Collectors.toList());
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0)).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSize(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED), m);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread(contexts, all);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }
}
