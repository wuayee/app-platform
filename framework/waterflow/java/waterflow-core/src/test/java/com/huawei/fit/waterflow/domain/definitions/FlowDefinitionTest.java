/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions;

import static com.huawei.fit.waterflow.common.ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.ARCHIVED;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.ERROR;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeStatus.PENDING;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.END;
import static com.huawei.fit.waterflow.domain.utils.WaterFlows.getPublisher;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.FlowsTestUtil;
import com.huawei.fit.waterflow.common.exceptions.WaterflowException;
import com.huawei.fit.waterflow.domain.context.FlowContext;
import com.huawei.fit.waterflow.domain.context.FlowData;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMemoRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.domain.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.domain.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.domain.parsers.FlowParser;
import com.huawei.fit.waterflow.domain.parsers.Parser;
import com.huawei.fit.waterflow.domain.stream.nodes.Blocks;
import com.huawei.fit.waterflow.domain.stream.nodes.From;
import com.huawei.fit.waterflow.domain.stream.nodes.Node;
import com.huawei.fit.waterflow.domain.stream.reactive.Publisher;
import com.huawei.fit.waterflow.domain.stream.reactive.Subscriber;
import com.huawei.fit.waterflow.domain.utils.UUIDUtil;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.util.CollectionUtils;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 流程定义核心测试类
 *
 * @author g00564732
 * @since 1.0
 */
@DisplayName("流程实例在内存中运行测试集合")
class FlowDefinitionTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Router ROUTER = Mockito.mock(Router.class);

    private static final Invoker MOCKED = Mockito.mock(Invoker.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    private static final FlowContextRepo<FlowData> REPO = new FlowContextMemoRepo<>();

    private static final FlowContextMessenger MESSENGER = new FlowContextMemoMessenger();

    private static final FlowLocks LOCKS = new FlowLocksMemo();

    private FlowEvent getEventById(String eventId, List<FlowEvent> events) {
        return events.stream().filter(event -> event.getMetaId().equals(eventId)).findAny().orElse(null);
    }

    @Nested
    @DisplayName("流程定义转化为flowable流程测试集合")
    class FlowConvertTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/convertors/";

        @Test
        @DisplayName("流程定义中只包含state节点的场景")
        void testFlowsConvertWithOnlyStateNode() {
            String jsonData = getJsonData(getFilePath("flows_with_state_node.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);

            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);

            assertFlowableFlow(flowDefinition, from);
        }

        @Test
        @DisplayName("流程定义中包含condition节点的场景")
        void testFlowsConvertWithConditionNode() {
            String jsonData = getJsonData(getFilePath("flows_with_conditional_node.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);

            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);

            assertFlowableFlow(flowDefinition, from);
        }

        private void assertFlowableFlow(FlowDefinition flowDefinition, Publisher<?> flowableNode) {
            Queue<Subscriber<?, ?>> flowableNodes = new ArrayDeque<>();
            Map<String, FlowNode> flowNodes = new HashMap<>();

            assertFlowableNode(flowableNode, flowDefinition);
            assertSubscription(flowableNode, flowDefinition, flowableNodes, flowNodes);

            while (!flowableNodes.isEmpty()) {
                Subscriber<?, ?> to = flowableNodes.poll();
                FlowNode flowNode = flowDefinition.getFlowNode(to.getId());
                // 结束节点没有subscription
                if (flowNode.belongTo(END)) {
                    assertEquals(to.getId(), flowNode.getMetaId());
                    assertTrue(flowNode.getEvents().isEmpty());
                    flowNodes.put(flowNode.getMetaId(), flowNode);
                    continue;
                }
                // 环路节点的to无需再验证
                if (flowNodes.containsKey(flowNode.getMetaId())) {
                    continue;
                }
                Node<?, ?> node = (Node<?, ?>) to;
                assertFlowableNode(node, flowDefinition);
                assertSubscription(node, flowDefinition, flowableNodes, flowNodes);
            }
            Set<String> flowNodeIdSet = flowDefinition.getNodeIdSet();
            Set<String> convertFlowNodeIdSet = flowNodes.keySet();
            assertEquals(flowNodeIdSet.size(), convertFlowNodeIdSet.size());
            assertTrue(CollectionUtils.equals(flowNodeIdSet, convertFlowNodeIdSet));
        }

        private void assertFlowableNode(Publisher<?> flowableNode, FlowDefinition flowDefinition) {
            FlowNode flowNode = flowDefinition.getFlowNode(flowableNode.getId());
            assertEquals(flowableNode.getStreamId(), flowDefinition.getStreamId());
            assertEquals(flowableNode.getId(), flowNode.getMetaId());
            assertEquals(flowableNode.getSubscriptions().size(), flowNode.getEvents().size());
        }

        private void assertSubscription(Publisher<?> flowableNode, FlowDefinition flowDefinition,
                Queue<Subscriber<?, ?>> flowableNodes, Map<String, FlowNode> flowNodes) {
            FlowNode flowNode = flowDefinition.getFlowNode(flowableNode.getId());
            List<FlowEvent> events = flowNode.getEvents();
            flowableNode.getSubscriptions().forEach(subscription -> {
                FlowEvent event = getEventById(subscription.getId(), events);
                assertNotNull(event);
                assertEquals(subscription.getId(), event.getMetaId());
                assertEquals(subscription.getTo().getId(), event.getTo());
                flowableNodes.offer(subscription.getTo());
            });
            flowNodes.put(flowNode.getMetaId(), flowNode);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例自动流转1_TO_1在内存持久化的场景测试集合")
    class FlowAutoExecutorInMemoryTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("测试流程实例自动流转1到1只有state节点的场景")
        void testFlowsExecutorWithOnlyStateNode1To1() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithOnlyStateNode1To1(flowData, contexts, all);
            assertEquals(flowData.getOperator(), contexts.get(0).getData().getOperator());
            assertEquals(flowData.getStartTime(), contexts.get(0).getData().getStartTime());
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
            flowNode.getJober().setProperties(null);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithError(flowDefinition, metaId, contexts, all, 2);
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
            flowNode.getJober().setProperties(null);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithError(flowDefinition, metaId, contexts, all, 3);
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1通过场景")
        void testFlowsExecutorWithConditionNodeFirstBranchTrue() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithConditionNodeFirstBranchTrue(flowData, contexts, all);
            assertEquals(flowData.getOperator(), contexts.get(0).getData().getOperator());
            assertEquals(flowData.getApplication(), contexts.get(0).getData().getApplication());
            assertEquals(flowData.getStartTime(), contexts.get(0).getData().getStartTime());
        }

        @Test
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1驳回场景")
        void testFlowsExecutorWithConditionNodeFirstBranchFalse() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            Map<String, Object> businessData = flowsExecutorWithConditionNodeFirstBranchTrue();
            businessData.put("cmc.approved", "false");
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
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
            businessData.put("committer.approved", "false");
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
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
            flowNode.getJober().setProperties(null);
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorConditionNodeWithError(flowDefinition, metaId, flowNode, contexts, all);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例手动流转1_TO_1在内存持久化的场景测试集合")
    class FlowManualExecutorInMemoryTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1包含condition节点人工任务分支1通过的场景测试")
        void testFlowsManualExecutorWithConditionNodeFirstBranchTrue() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsManualExecutorWithConditionNodeFirstBranchTrue(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, all);

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            From<FlowData> resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            Blocks.Block<FlowData> block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "true");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Lists.newArrayList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> resumeContexts = FlowsTestUtil.waitSingle(
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeFirstBranchTrueBeforeBlock(metaId, contexts, all);

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            From<FlowData> resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            Blocks.Block<FlowData> block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "false");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Lists.newArrayList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> resumeContexts = FlowsTestUtil.waitSingle(
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 2, PENDING);
            assertNull(contexts.get(0).getToBatch());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext = contexts.get(0);
            From<FlowData> resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            Blocks.Block<FlowData> block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "transferred");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Lists.newArrayList(resumeContext));

            metaId = "event5";
            contexts = FlowsTestUtil.waitSingle(contextSupplier(REPO, streamId, traceId, metaId, PENDING));
            all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 4, PENDING);
            assertNull(contexts.get(0).getToBatch());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            resumeContext = contexts.get(0);
            resumeFrom = (From<FlowData>) getPublisher(resumeContext.getStreamId());
            block = resumeFrom.getBlock(resumeContext.getPosition());
            resumeContext.getData().getBusinessData().put("status", "approved");
            resumeContext.toBatch(UUIDUtil.uuid());
            REPO.updateFlowData(Collections.singletonList(resumeContext));
            block.process(Lists.newArrayList(resumeContext));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsManualExecutorWithConditionNodeCircle(metaId, contexts, all, 7, ARCHIVED);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例自动流转1_TO_1JoberWithFitable节点任务在内存持久化的场景测试集合")
    class FlowAutoExecutorInMemoryWithJoberIncludeFitableTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1只有state节点的执行GeneralJober任务场景测试")
        void testFlowsExecuteGeneralJober() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> output = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            data.put("businessData", flowData.getBusinessData());
            output.add(data);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(MOCKED);
            when(MOCKED.timeout(anyLong(), any())).thenReturn(MOCKED);
            when(MOCKED.invoke(any())).thenReturn(output);

            String traceId = from.offer(flowData);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteGeneralJober(contexts, all);
            assertEquals(flowData.getOperator(), contexts.get(0).getData().getOperator());
            assertEquals(flowData.getApplication(), contexts.get(0).getData().getApplication());
            assertEquals(flowData.getStartTime(), contexts.get(0).getData().getStartTime());
        }

        @Test
        @DisplayName("测试实例自动流转state节点执行GeneralJober执行失败抛出异常")
        void testFlowsExecuteGeneralJoberError() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            String metaId = "state1";
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            Router router = Mockito.mock(Router.class);
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(router);
            Invoker invoker = Mockito.mock(Invoker.class);
            when(router.route(any())).thenReturn(invoker);
            when(invoker.timeout(anyLong(), any())).thenReturn(invoker);
            when(invoker.invoke(anyList())).thenThrow(new WaterflowException(FLOW_EXECUTE_FITABLE_TASK_FAILED));
            when(invoker.invoke(any(), anyList(), anyString())).thenReturn(null);

            String traceId = from.offer(flowData);

            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecuteGeneralJoberError(invoker, flowDefinition, metaId, contexts, all);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例流转M_TO_N在内存持久化的场景测试集合")
    class FlowAutoExecutorInMemoryMToNTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer一个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferOneData() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData("true"), "gsy"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData("true"), "yyk"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData("false"), "yxy"));
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = list.stream().map(f -> {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> businessData = f.getBusinessData();
                result.put("businessData", businessData);
                return result;
            }).collect(Collectors.toList());
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(MOCKED);
            when(MOCKED.timeout(anyLong(), any())).thenReturn(MOCKED);
            when(MOCKED.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED), 3);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNForOfferOneData(contexts, all);
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer三个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferMultiData() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData[] flowData = {
                    getFlowData(flowsExecuteProduceFromMToNForOfferOneData("true"), "gsy"),
                    getFlowData(flowsExecuteProduceFromMToNForOfferOneData("true"), "yyk"),
                    getFlowData(flowsExecuteProduceFromMToNForOfferOneData("false"), "yxy")
            };
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = Lists.newArrayList(flowData).stream().map(f -> {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> businessData = f.getBusinessData();
                result.put("businessData", businessData);
                return result;
            }).collect(Collectors.toList());
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(MOCKED);
            when(MOCKED.timeout(anyLong(), any())).thenReturn(MOCKED);
            when(MOCKED.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(flowData);

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED), 3);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNForOfferMultiData(contexts, all);
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点包含前后过滤器的场景测试")
        void testFlowsExecuteFilterFromMToN() {
            String jsonData = getJsonData(getFilePath("flows_manual_echo_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData flowData1 = getFlowData(flowsExecuteFilterFromMToN(), "gsy");
            FlowData flowData2 = getFlowData(flowsExecuteFilterFromMToN(), "yyk");
            FlowData flowData3 = getFlowData(flowsExecuteFilterFromMToN(), "yxy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String eventMetaId = "event1"; // 来自json文件中的配置
            String stateMetaId = "state1"; // 来自json文件中的配置

            String traceId1 = from.offer(flowData1);
            List<FlowContext<FlowData>> contexts1 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId1, eventMetaId, PENDING));
            assertEquals(1, contexts1.size());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext1 = contexts1.get(0);
            From<FlowData> resumeFrom1 = (From<FlowData>) getPublisher(resumeContext1.getStreamId());
            Blocks.Block<FlowData> block1 = resumeFrom1.getBlock(resumeContext1.getPosition());
            String toBatch1 = UUIDUtil.uuid();
            contexts1.forEach(c -> {
                c.getData().getBusinessData().put("approve.status", "true");
                c.toBatch(toBatch1);
            });
            REPO.save(contexts1);
            block1.process(contexts1);
            contexts1 = FlowsTestUtil.waitFortyMillis(contextSupplier(REPO, streamId, traceId1, eventMetaId, PENDING));
            assertEquals(1, contexts1.size());

            String traceId2 = from.offer(flowData2);
            List<FlowContext<FlowData>> contexts2 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId2, eventMetaId, PENDING));
            assertEquals(1, contexts2.size());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext2 = contexts2.get(0);
            From<FlowData> resumeFrom2 = (From<FlowData>) getPublisher(resumeContext2.getStreamId());
            Blocks.Block<FlowData> block2 = resumeFrom2.getBlock(resumeContext2.getPosition());
            String toBatch2 = UUIDUtil.uuid();
            contexts2.forEach(c -> {
                c.getData().getBusinessData().put("approve.status", "true");
                c.toBatch(toBatch2);
            });
            REPO.save(contexts2);
            block2.process(contexts2);
            contexts2 = FlowsTestUtil.waitFortyMillis(contextSupplier(REPO, streamId, traceId2, eventMetaId, PENDING));
            assertEquals(1, contexts2.size());

            String traceId3 = from.offer(flowData3);
            List<FlowContext<FlowData>> contexts3 = FlowsTestUtil.waitSingle(
                    contextSupplier(REPO, streamId, traceId3, eventMetaId, PENDING));
            assertEquals(1, contexts3.size());

            FlowsTestUtil.waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext3 = contexts3.get(0);
            From<FlowData> resumeFrom3 = (From<FlowData>) getPublisher(resumeContext3.getStreamId());
            Blocks.Block<FlowData> block3 = resumeFrom3.getBlock(resumeContext3.getPosition());
            String toBatch3 = UUIDUtil.uuid();
            contexts3.forEach(c -> {
                c.getData().getBusinessData().put("approve.status", "false");
                c.toBatch(toBatch3);
            });
            REPO.save(contexts3);
            block3.process(contexts3);
            contexts3 = FlowsTestUtil.waitEmpty(contextSupplier(REPO, streamId, traceId3, eventMetaId, PENDING));
            assertEquals(0, contexts3.size());

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> endContexts = FlowsTestUtil.waitSize(
                    contextSupplier(REPO, streamId, traceId1, flowNode.getMetaId(), ARCHIVED), 3);
            assertFlowsExecuteFilterFromMToN(REPO, traceId1, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId2, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId3, endContexts);
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例流转M_TO_N添加过滤器后按批次流转在内存持久化的场景测试集合")
    class FlowAutoExecutorInPersistMToNWithFilterMinimumSizeOne extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例m到n最小SIZE过滤器为1单线程offer的场景测试")
        void testFlowsExecuteProduceFromMToNWithMinimumSizeOneInSingleThread() throws Throwable {
            String jsonData = getJsonData(
                    getFilePath("flows_auto_general_jober_state_node_m_to_n_with_filter_batch_size_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            int m = 5;
            for (int j = 0; j < m; j++) {
                list.add(getFlowData(flowsExecuteFilterFromMToN(), "gsy"));
            }
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = list.stream().map(f -> {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> businessData = f.getBusinessData();
                result.put("businessData", businessData);
                return result;
            }).collect(Collectors.toList());
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(MOCKED);
            when(MOCKED.timeout(anyLong(), any())).thenReturn(MOCKED);
            when(MOCKED.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0));

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = FlowsTestUtil.waitSize(
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
