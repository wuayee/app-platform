/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions;

import static com.huawei.fit.jober.FlowsTestUtil.waitEmpty;
import static com.huawei.fit.jober.FlowsTestUtil.waitFortyMillis;
import static com.huawei.fit.jober.FlowsTestUtil.waitMillis;
import static com.huawei.fit.jober.FlowsTestUtil.waitSingle;
import static com.huawei.fit.jober.FlowsTestUtil.waitSize;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_EXECUTE_ASYNC_JOBER_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.FLOW_EXECUTE_FITABLE_TASK_FAILED;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ARCHIVED;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.ERROR;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PENDING;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus.PROCESSING;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType.END;
import static com.huawei.fit.waterflow.flowsengine.utils.WaterFlows.getPublisher;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.FlowsDataBaseTest;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.common.utils.UUIDUtil;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMemoRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowEchoJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Publisher;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.FitStream.Subscriber;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.From;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import com.huawei.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import com.huawei.fit.waterflow.flowsengine.utils.FlowUtil;
import com.huawei.fit.waterflow.graph.util.FlowDefinitionParseUtils;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
 * @author 高诗意
 * @since 2023/08/22
 */
@DisplayName("流程实例在内存中运行测试集合")
@Disabled
class FlowDefinitionTest {
    private static final BrokerClient BROKER_CLIENT = Mockito.mock(BrokerClient.class);

    private static final Router ROUTER = Mockito.mock(Router.class);

    private static final Invoker INVOKER = Mockito.mock(Invoker.class);

    private static final Parser PARSER = new FlowParser(BROKER_CLIENT);

    private static final FlowContextMessenger MESSENGER = new FlowContextMemoMessenger();

    private static final FlowLocks LOCKS = new FlowLocksMemo();

    private static final FlowContextRepo<FlowData> REPO = new FlowContextMemoRepo<>();

    private FlowEvent getEventById(String eventId, List<FlowEvent> events) {
        return events.stream().filter(event -> event.getMetaId().equals(eventId)).findAny().orElse(null);
    }

    @Nested
    @DisplayName("带条件节点的Elsa数据转化为flowable流程测试集合")
    class FlowConvertTestWithElsaConditionNode extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/convertors/";

        @Test
        @DisplayName("测试解析Elsa条件事件成功")
        public void testElsaConditionParserSuccess() {
            String jsonData = getJsonData(getFilePath("flows_with_elsa_condition.json"));

            FlowDefinition flowDefinition = PARSER.parse(
                    FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(jsonData), "1.0"));
            flowDefinition.setDefinitionId(UUIDUtil.uuid());
            flowDefinition.setMetaId(UUIDUtil.uuid());
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);

            HashMap<String, Object> businessData = new HashMap<String, Object>() {{
                put("Operator", "crx");
                put("bool1", false);
                put("bool2", false);
            }};
            FlowUtil.cacheResultToNode(businessData, from.getId());
            String traceId = from.offer(getFlowData(businessData, "crx")).getTraceId();
            String streamId = flowDefinition.getStreamId();
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, "elsaShapesoux52", ARCHIVED));

            assertEquals("elsaShapesoux52", contexts.get(0).getPosition());
        }

        @Test
        @DisplayName("测试解析Elsa条件事件并走Else逻辑成功")
        public void testElsaConditionParserAndRunElseBranchSuccess() {
            String jsonData = getJsonData(getFilePath("flows_with_elsa_condition.json"));

            FlowDefinition flowDefinition = PARSER.parse(
                    FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(jsonData), "1.0"));
            flowDefinition.setDefinitionId(UUIDUtil.uuid());
            flowDefinition.setMetaId(UUIDUtil.uuid());
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);

            HashMap<String, Object> businessData = new HashMap<String, Object>() {{
                put("Operator", "crx");
                put("bool1", false);
                put("bool2", true);
            }};
            FlowUtil.cacheResultToNode(businessData, from.getId());
            String traceId = from.offer(getFlowData(businessData, "crx")).getTraceId();
            String streamId = flowDefinition.getStreamId();
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, "elsaShapesoux53", ARCHIVED));

            assertEquals("elsaShapesoux53", contexts.get(0).getPosition());
        }

        @Test
        @DisplayName("测试解析带有branchType的Elsa条件事件并走Else逻辑成功")
        public void testElsaConditionParserWithBranchTypeThenRunElseBranchSuccess() {
            String jsonData = getJsonData(getFilePath("flows_with_elsa_condition_with_branch_type.json"));

            FlowDefinition flowDefinition = PARSER.parse(
                    FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(jsonData), "1.0"));
            flowDefinition.setDefinitionId(UUIDUtil.uuid());
            flowDefinition.setMetaId(UUIDUtil.uuid());
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);

            HashMap<String, Object> businessData = new HashMap<String, Object>() {{
                put("Operator", "crx");
                put("bool1", false);
                put("bool2", true);
            }};
            FlowUtil.cacheResultToNode(businessData, from.getId());
            String traceId = from.offer(getFlowData(businessData, "crx")).getTraceId();
            String streamId = flowDefinition.getStreamId();
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, "elsaShapesoux52", ARCHIVED));

            assertEquals("elsaShapesoux52", contexts.get(0).getPosition());
        }

        @Test
        @DisplayName("测试解析字段类型为String的条件并走第二个分支逻辑成功")
        public void testStringTypeConditionThenRunSecondBranchSuccess() {
            String jsonData = getJsonData(getFilePath("flows_with_elsa_string_condition.json"));

            FlowDefinition flowDefinition = PARSER.parse(
                    FlowDefinitionParseUtils.getParsedGraphData(JSONObject.parseObject(jsonData), "1.0"));
            flowDefinition.setDefinitionId(UUIDUtil.uuid());
            flowDefinition.setMetaId(UUIDUtil.uuid());
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);

            HashMap<String, Object> businessData = new HashMap<String, Object>() {{
                put("Operator", "crx");
                put("Question", "   ");
            }};
            FlowUtil.cacheResultToNode(businessData, from.getId());
            String traceId = from.offer(getFlowData(businessData, "crx")).getTraceId();
            String streamId = flowDefinition.getStreamId();
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, "elsaShapesoux52", ARCHIVED));

            assertEquals("elsaShapesoux52", contexts.get(0).getPosition());
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
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

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecutorWithOnlyStateNode1To1(flowData, contexts, all);
            assertEquals(flowData.getOperator(), contexts.get(0).getData().getOperator());
            assertEquals(flowData.getStartTime(), contexts.get(0).getData().getStartTime());
        }

        @Test
        @Disabled
        @DisplayName("测试流程实例异步job执行")
        void testFlowsExecutorWithAsyncJob() {
            String jsonData = getJsonData(getFilePath("flows_auto_async_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setDefinitionId("successAsyncJob");
            flowDefinition.setMetaId("successAsyncJob");
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            String asyncNodeId = "state1";
            FlowNode asyncFlowNode = flowDefinition.getFlowNode(asyncNodeId);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, asyncFlowNode.getMetaId(), PROCESSING));
            List<FlowContext<FlowData>> allContexts = this.getContextsByTraceWrapper(REPO, traceId);

            assertEquals(1, contexts.size());
            assertEquals(PROCESSING, contexts.get(0).getStatus());
            assertEquals(2, allContexts.size());

            // 自行执行job, 生成新的contexts，推进流程
            FlowEchoJober flowEchoJober = new FlowEchoJober();
            flowEchoJober.setProperties(new HashMap<>());
            flowEchoJober.setParentNode(asyncFlowNode);
            List<FlowContext<FlowData>> newContexts = flowEchoJober.execute(
                            Collections.singletonList(contexts.get(0).getData()))
                    .stream()
                    .map(data -> contexts.get(0).generate(data, asyncNodeId, LocalDateTime.now()))
                    .collect(Collectors.toList());

            Node<FlowData, FlowData> asyncNode = from.findNodeFromFlow(from, asyncNodeId);
            asyncNode.afterProcess(contexts, newContexts);
            // assert
            FlowNode endNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> endContexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, endNode.getMetaId(), ARCHIVED));
            allContexts = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorWithOnlyStateNode1To1(flowData, endContexts, allContexts);

            assertEquals(flowData.getOperator(), endContexts.get(0).getData().getOperator());
            assertEquals(flowData.getStartTime(), endContexts.get(0).getData().getStartTime());
        }

        @Test
        @Disabled
        @DisplayName("测试流程实例异步job执行失败的场景")
        void shouldReturnErrorContextWhenExecuteWithAsyncJobAndSetFail() {
            String jsonData = getJsonData(getFilePath("flows_auto_async_echo_state_node_1_to_1.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            flowDefinition.setDefinitionId("failAsyncJob");
            flowDefinition.setMetaId("failAsyncJob");
            String expectErrorMessage = "execute fail";
            FlowData flowData = getFlowData(flowsExecutorWithOnlyStateNode1To1(), "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            String asyncNodeId = "state1";
            FlowNode asyncFlowNode = flowDefinition.getFlowNode(asyncNodeId);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, asyncFlowNode.getMetaId(), PROCESSING));

            Node<FlowData, FlowData> asyncNode = from.findNodeFromFlow(from, asyncNodeId);
            JobberException expectException = new JobberException(FLOW_EXECUTE_ASYNC_JOBER_FAILED, expectErrorMessage);
            asyncNode.setFailed(contexts, expectException);
            // assert
            List<FlowContext<FlowData>> errorContexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, asyncNode.getId(), ERROR));
            List<FlowContext<FlowData>> allContexts = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, asyncNodeId, errorContexts, allContexts, 2,
                    expectException);

            assertEquals(flowData.getOperator(), errorContexts.get(0).getData().getOperator());
            assertEquals(flowData.getStartTime(), errorContexts.get(0).getData().getStartTime());
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(contextSupplier(REPO, streamId, traceId, metaId, ERROR));
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 3, expectException);
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

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
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
            businessData.put("cmc", new HashMap<String, Object>() {
                {
                    put("approved", "false");
                }
            });
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
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
        @DisplayName("测试流程实例自动流转1到1包含condition节点的分支1通过{{reject}}字段来进行驳回的场景")
        void testFlowsExecutorWithConditionNodeRejectBranch() {
            String jsonData = getJsonData(getFilePath("flows_auto_echo_with_condition_node_1_to_1_reject.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            Map<String, Object> businessData = flowsExecutorWithConditionNodeFirstBranchTrue();
            businessData.put("cmc", new HashMap<String, Object>() {
                {
                    put("reject", "true");
                }
            });
            FlowData flowData = getFlowData(businessData, "gsy");
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED));
            assertEquals(3, this.getContextsByTraceWrapper(REPO, traceId).size());
            Map<String, Object> resultBusinessData = contexts.get(0).getData().getBusinessData();
            assertEquals("success", resultBusinessData.get("approvedResult"));
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            String traceId = from.offer(flowData).getTraceId();

            List<FlowContext<FlowData>> contexts = waitSingle(contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecutorStateNodeWithException(flowDefinition, metaId, contexts, all, 3, expectException);
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
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
            resumeContext.getData().getBusinessData().put("status", "false");
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String metaId = "event1"; // 来自json文件中的配置

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
    @DisplayName("流程实例自动流转1_TO_1JoberWithFitable节点任务在内存持久化的场景测试集合")
    class FlowAutoExecutorInMemoryWithJoberIncludeFitableTest extends FlowsDataBaseTest {
        private static final String FILE_PATH_PREFIX = "flows/executors/";

        @Test
        @DisplayName("流程实例1到1只有state节点的执行GeneralJober任务场景测试")
        void testFlowsExecuteGeneralJober() {
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
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(output);

            String traceId = from.offer(flowData).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSingle(
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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(anyList())).thenThrow(new JobberException(FLOW_EXECUTE_FITABLE_TASK_FAILED));
            when(INVOKER.invoke(any(), anyList(), anyString())).thenReturn(null);

            String traceId = from.offer(flowData).getTraceId();

            String metaId = "state1";
            List<FlowContext<FlowData>> contexts = waitSingle(contextSupplier(REPO, streamId, traceId, metaId, ERROR));
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);
            assertFlowsExecuteGeneralJoberError(INVOKER, flowDefinition, metaId, contexts, all);
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
        void testFlowsExecuteProduceFromMToNForOfferOneData() {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            List<FlowData> list = new ArrayList<>();
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "gsy"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "yyk"));
            list.add(getFlowData(flowsExecuteProduceFromMToNForOfferOneData(false), "yxy"));
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
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(list.get(0)).getTraceId();

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> contexts = waitSize(
                    contextSupplier(REPO, streamId, traceId, flowNode.getMetaId(), ARCHIVED), 3);
            List<FlowContext<FlowData>> all = this.getContextsByTraceWrapper(REPO, traceId);

            assertFlowsExecuteProduceFromMToNForOfferOneData(contexts, all);
        }

        @Test
        @DisplayName("流程实例m到n包含condition节点一次只offer三个数据的场景测试")
        void testFlowsExecuteProduceFromMToNForOfferMultiData() throws Throwable {
            String jsonData = getJsonData(getFilePath("flows_auto_general_jober_with_condition_node_m_to_n.json"));
            FlowDefinition flowDefinition = PARSER.parse(jsonData);
            FlowData[] flowData = new FlowData[] {
                    getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "gsy"),
                    getFlowData(flowsExecuteProduceFromMToNForOfferOneData(true), "yyk"),
                    getFlowData(flowsExecuteProduceFromMToNForOfferOneData(false), "yxy")
            };
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);

            List<Map<String, Object>> outputs = Arrays.asList(flowData).stream().map(f -> {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> businessData = f.getBusinessData();
                result.put("businessData", businessData);
                return result;
            }).collect(Collectors.toList());
            when(BROKER_CLIENT.getRouter(any(), anyString())).thenReturn(ROUTER);
            when(ROUTER.route(any())).thenReturn(INVOKER);
            when(INVOKER.communicationType(any())).thenReturn(INVOKER);
            when(INVOKER.invoke(any())).thenReturn(outputs);

            String traceId = from.offer(flowData).getTraceId();

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
            From<FlowData> from = (From<FlowData>) flowDefinition.convertToFlow(REPO, MESSENGER, LOCKS);
            String streamId = flowDefinition.getStreamId();
            assertSingleInstance(getPublisher(streamId), from);
            String eventMetaId = "event1"; // 来自json文件中的配置

            String traceId1 = from.offer(flowData1).getTraceId();
            List<FlowContext<FlowData>> contexts1 = waitSingle(
                    contextSupplier(REPO, streamId, traceId1, eventMetaId, PENDING));
            assertEquals(1, contexts1.size());

            checkContext(contexts1, streamId, traceId1, eventMetaId);

            FlowData flowData2 = getFlowData(flowsExecuteFilterFromMToN(), "yyk");
            String traceId2 = from.offer(flowData2).getTraceId();
            List<FlowContext<FlowData>> contexts2 = waitSingle(
                    contextSupplier(REPO, streamId, traceId2, eventMetaId, PENDING));
            assertEquals(1, contexts2.size());

            checkContext(contexts2, streamId, traceId2, eventMetaId);

            FlowData flowData3 = getFlowData(flowsExecuteFilterFromMToN(), "yxy");
            String traceId3 = from.offer(flowData3).getTraceId();
            List<FlowContext<FlowData>> contexts3 = waitSingle(
                    contextSupplier(REPO, streamId, traceId3, eventMetaId, PENDING));
            assertEquals(1, contexts3.size());

            waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext3 = contexts3.get(0);
            From<FlowData> resumeFrom3 = (From<FlowData>) getPublisher(resumeContext3.getStreamId());
            Blocks.Block<FlowData> block3 = resumeFrom3.getBlock(resumeContext3.getPosition());
            String toBatch3 = UUIDUtil.uuid();
            contexts3.forEach(c -> {
                c.getData().getBusinessData().put("approve", new HashMap<String, Boolean>() {
                    {
                        put("status", false);
                    }
                });
                c.toBatch(toBatch3);
            });
            REPO.save(contexts3);
            block3.process(contexts3);
            contexts3 = waitEmpty(contextSupplier(REPO, streamId, traceId3, eventMetaId, PENDING));
            assertEquals(0, contexts3.size());

            FlowNode flowNode = flowDefinition.getFlowNode(END);
            List<FlowContext<FlowData>> endContexts = waitSize(
                    contextSupplier(REPO, streamId, traceId1, flowNode.getMetaId(), ARCHIVED), 3);
            assertFlowsExecuteFilterFromMToN(REPO, traceId1, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId2, endContexts);
            assertFlowsExecuteFilterFromMToN(REPO, traceId3, endContexts);
        }

        private void checkContext(List<FlowContext<FlowData>> contexts, String streamId, String traceId1,
                String eventMetaId) {
            waitMillis(Collections::emptyList, 100);
            FlowContext<FlowData> resumeContext1 = contexts.get(0);
            From<FlowData> resumeFrom1 = (From<FlowData>) getPublisher(resumeContext1.getStreamId());
            Blocks.Block<FlowData> block1 = resumeFrom1.getBlock(resumeContext1.getPosition());
            String toBatch1 = UUIDUtil.uuid();
            contexts.forEach(c -> {
                c.getData().getBusinessData().put("approve", new HashMap<String, Boolean>() {
                    {
                        put("status", true);
                    }
                });
                c.toBatch(toBatch1);
            });
            REPO.save(contexts);
            block1.process(contexts);
            List<FlowContext<FlowData>> newContexts = waitFortyMillis(
                    contextSupplier(REPO, streamId, traceId1, eventMetaId, PENDING));
            assertEquals(1, newContexts.size());
        }

        @Override
        protected String getFilePathPrefix() {
            return FILE_PATH_PREFIX;
        }
    }

    @Nested
    @DisplayName("流程实例流转M_TO_N添加过滤器后按批次流转在内存持久化的场景测试集合")
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
