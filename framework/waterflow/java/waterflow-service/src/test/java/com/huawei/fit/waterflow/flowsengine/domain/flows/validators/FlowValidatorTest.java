/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.validators;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeTriggerMode.AUTO;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeTriggerMode.MANUAL;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType.END;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType.START;
import static com.huawei.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType.STATE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowEndNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowStartNode;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowGeneralCallback;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowEchoJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowGeneralJober;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.FlowParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.Parser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.CallbacksRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.DefinitionRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.EventsRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.FlowRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.JobersRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.NodesRule;
import com.huawei.fit.waterflow.flowsengine.domain.flows.validators.rules.TasksRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 流程定义校验器测试类
 *
 * @author g00564732
 * @since 2023/08/30
 */
@Disabled
class FlowValidatorTest extends FlowsDataBaseTest {
    /**
     * 第二个节点id
     */
    public static final String OTHER_STATE_ID = "state2";

    /**
     * 第三个节点id
     */
    public static final String THIRD_STATE_ID = "state3";

    private static final String FILE_PATH_PREFIX = "flows/validators/";

    private static final String META_ID = "metaId";

    private static final String NAME = "name";

    private static final String TENANT = "tenant";

    private static final String START_ID = "start1";

    private static final String STATE_ID = "state1";

    private static final String CONDITION_ID = "condi1";

    private static final String END_ID = "ender1";

    private static final String EMPTY = "";

    private String errorMessage(String param) {
        return MessageFormat.format(INPUT_PARAM_IS_INVALID.getMessage(), param);
    }

    private FlowNode getStartNode() {
        List<FlowEvent> events = new ArrayList<>();
        events.add(FlowEvent.builder().metaId(META_ID).name(NAME).from(START_ID).to(STATE_ID).build());
        FlowNode flowNode = new FlowStartNode();
        flowNode.setType(START);
        return flowNode;
    }

    private FlowNode getEndNode() {
        FlowNode flowNode = new FlowEndNode();
        flowNode.setType(END);
        return flowNode;
    }

    @Override
    protected String getFilePathPrefix() {
        return FILE_PATH_PREFIX;
    }

    @Nested
    @DisplayName("流程定义校验规则测试集合")
    class DefinitionRuleTest {
        private Validator flowValidator;

        private Parser parser;

        private FlowDefinition flowDefinition;

        @BeforeEach
        void setUp() {
            List<FlowRule> flowRules = new ArrayList<>();
            flowRules.add(new DefinitionRule());
            flowRules.add(new NodesRule());
            flowRules.add(new EventsRule());
            flowRules.add(new JobersRule());
            flowRules.add(new TasksRule(null));
            flowValidator = new FlowValidator(flowRules);
            parser = new FlowParser(null);
            flowDefinition = getFlowDefinitionFromJson();
        }

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(getFilePath("general_flows_with_fitable_and_manual_task.json"));
            flowDefinition = parser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @Test
        @DisplayName("测试流程定义校验成功")
        public void testValidateSuccess() {
            assertDoesNotThrow(() -> flowValidator.validate(flowDefinition));
        }

        @Test
        @DisplayName("测试流程定义flowContext配置校验失败")
        public void testValidateFail() {
            HashMap<String, Object> newProperties = new HashMap<>();
            newProperties.put("flowContext", "{{}}");
            flowDefinition.getFlowNode(STATE_ID).setProperties(newProperties);

            JobberParamException nullException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flowContext has been config, but the output key is empty"),
                    nullException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义MetaId校验成功")
        public void testValidateDefinitionMetaIdNotNullSuccess() {
            flowDefinition.setMetaId(EMPTY);
            JobberParamException nullException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition metaId, metaId can not be blank"), nullException.getMessage());

            flowDefinition.setMetaId("11");
            JobberParamException lengthException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition metaId, metaId length must be 32"),
                    lengthException.getMessage());

            flowDefinition.setMetaId("apimckapimckapimckapimckapimcka!");
            JobberParamException specialCharException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition metaId, metaId contains special characters"),
                    specialCharException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Name校验成功")
        public void testValidateDefinitionNameSuccess() {
            flowDefinition.setName(EMPTY);
            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition name, name can not be blank"), exception.getMessage());

            flowDefinition.setName("apimckapimckapimckapimckapimckapapimckapimckapimckapimckapimckapap"
                    + "imckapimckapimckapimckapimckapapimckapimckapimckapimckapimcka"
                    + "papimckapimckapimckapimckapimckapapimckapimckapimckapimckapim"
                    + "ckapapimckapimckapimckapimckapimckapapimckapimckapimckapimckapimckap1");
            JobberParamException lengthException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition name, name length over 256"), lengthException.getMessage());

            flowDefinition.setName("name@");
            JobberParamException specialCharException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition name, name contains special characters"),
                    specialCharException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Version校验成功")
        public void testValidateDefinitionVersionSuccess() {
            flowDefinition.setVersion(EMPTY);
            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition version, version can not be blank"), exception.getMessage());

            flowDefinition.setVersion("1");
            JobberParamException formatException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition version, version format must be X.Y.Z"),
                    formatException.getMessage());

            flowDefinition.setVersion("1.0.str");
            JobberParamException stringException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition version, version format must be X.Y.Z"),
                    stringException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Tenant校验成功")
        public void testValidateDefinitionTenantSuccess() {
            flowDefinition.setTenant(EMPTY);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition tenant, tenant can not be blank"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义status校验成功")
        public void testValidateDefinitionStatusSuccess() {
            flowDefinition.setStatus(null);
            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition status, status can not be blank"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Nodes校验成功")
        public void testValidateDefinitionNodesSuccess() {
            flowDefinition.setNodeMap(new HashMap<>());

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition nodes, nodes can not be empty"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义NodeSize校验成功")
        public void testValidateDefinitionNodeSizeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.remove(CONDITION_ID);
            nodeMap.remove(STATE_ID);
            nodeMap.remove(OTHER_STATE_ID);
            nodeMap.remove(THIRD_STATE_ID);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("all node number, node number must more than 3"), exception.getMessage());

            flowDefinition.setNodeMap(new HashMap<>());
            JobberParamException nullException = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow definition nodes, nodes can not be empty"), nullException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义StartNodeSize校验成功")
        public void testValidateDefinitionStartNodeSizeLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.remove(START_ID);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node number"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义StartNodeSize校验成功")
        public void testValidateDefinitionStartNodeSizeGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.put("temp", getStartNode());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node number"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义EndNodeSize校验成功")
        public void testValidateDefinitionEndNodeSizeLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.remove(END_ID);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("end node number"), exception.getMessage());
        }

        @Test
        @Disabled("当前流程支持大于1个结束节点")
        @DisplayName("测试流程定义EndNodeSize校验成功")
        public void testValidateDefinitionEndNodeSizeGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.put("temp", getEndNode());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("end node number"), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("流程节点校验规则测试集合")
    class NodesRuleTest {
        private Validator flowNodeValidator;

        private Parser flowParser;

        private FlowDefinition flowDefinition;

        @BeforeEach
        void setUp() {
            List<FlowRule> flowRules = new ArrayList<>();
            flowRules.add(new DefinitionRule());
            flowRules.add(new NodesRule());
            flowRules.add(new EventsRule());
            flowRules.add(new JobersRule());
            flowRules.add(new TasksRule(null));
            flowNodeValidator = new FlowValidator(flowRules);
            flowParser = new FlowParser(null);
            flowDefinition = getFlowDefinitionFromJson();
        }

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(getFilePath("general_flows_with_fitable_and_manual_task.json"));
            flowDefinition = flowParser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @Test
        @DisplayName("测试流程节点MetaId校验成功")
        public void testValidateNodeMetaIdSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setMetaId(EMPTY);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow node metaId, metaId can not be blank"), exception.getMessage());

            nodeMap.get(START_ID).setMetaId("start%");
            exception = assertThrows(JobberParamException.class, () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow node metaId not allow special char"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点Name校验成功")
        public void testValidateNodeNameSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(CONDITION_ID).setName(EMPTY);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow node name, name can not be blank"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点Type校验成功")
        public void testValidateNodeVersionSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(END_ID).setType(null);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow node type, node type can not be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点TriggerMode校验成功")
        public void testValidateNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).setTriggerMode(null);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow node trigger mode, can not be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点手动任务id校验成功")
        public void testValidateNodeTaskIdSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).getTask().setTaskId(null);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow APPROVING_TASK task id"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点手动任务类型校验成功")
        public void testValidateNodeTaskTypeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).getTask().setTaskType(null);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow task type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点手动任务属性校验成功")
        public void testValidateNodeTaskPropertiesSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).getTask().getProperties().remove("title");
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow task title"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点自动任务类型校验成功")
        public void testValidateNodeJoberTypeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state2").getJober().setType(null);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow jober type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点自动任务fitables校验成功")
        public void testValidateNodeJoberFitablesSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state2").getJober().setFitables(null);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow jober fitables"), exception.getMessage());

            nodeMap.get("state2").getJober().setFitables(new HashSet<>());
            flowDefinition.setNodeMap(nodeMap);

            exception = assertThrows(JobberParamException.class, () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow jober fitables"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点自动任务HttpJober校验成功")
        public void testValidateNodeHttpJoberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state3").getJober().setProperties(new HashMap<>());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow http jober entity"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点过滤器校验成功")
        public void testValidateNodeFilterSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state1").getTaskFilter().getProperties().put("threshold", "a");
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow filter threshold"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeTriggerMode校验成功")
        public void testValidateStartNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setTriggerMode(MANUAL);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node trigger mode"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeTriggerMode校验成功")
        public void testValidateStartNodeJoberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setTriggerMode(AUTO);
            nodeMap.get(START_ID).setJober(new FlowEchoJober());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node jober should be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeEventLessThan1校验成功")
        public void testValidateStartNodeEventLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setEvents(new ArrayList<>());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeEventGreatThan1校验成功")
        public void testValidateStartNodeEventGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            events.add(new FlowEvent());
            nodeMap.get(START_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeJobber校验成功")
        public void testValidateStartNodeJobberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            FlowEchoJober flowFitableJobber = new FlowEchoJober();
            nodeMap.get(START_ID).setJober(flowFitableJobber);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("start node jober should be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StateNodeEventLessThan1校验成功")
        public void testValidateStateNodeEventLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).setEvents(new ArrayList<>());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("state node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StateNodeEventGreatThan1校验成功")
        public void testValidateStateNodeEventGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            events.add(new FlowEvent());
            nodeMap.get(STATE_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("state node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StateNodeJobber校验成功")
        public void testValidateStateNodeJobberSuccess() {
            assertDoesNotThrow(() -> flowNodeValidator.validate(flowDefinition));
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeTriggerMode校验成功")
        public void testValidateConditionNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(CONDITION_ID).setTriggerMode(MANUAL);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("condition node trigger mode"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeEventLessThan1校验成功")
        public void testValidateConditionNodeEventLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(CONDITION_ID).setEvents(new ArrayList<>());
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("condition node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeEventEqual1校验成功")
        public void testValidateConditionNodeEventEqual1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            nodeMap.get(CONDITION_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("condition node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeJobber校验成功")
        public void testValidateConditionNodeJobberSuccess() {
            assertDoesNotThrow(() -> flowNodeValidator.validate(flowDefinition));
        }

        @Test
        @DisplayName("测试流程节点EndNodeTriggerMode校验成功")
        public void testValidateEndNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(END_ID).setTriggerMode(MANUAL);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("end node trigger mode"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点EndNode自动任务校验成功")
        public void testValidateEndNodeJoberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(END_ID).setJober(new FlowGeneralJober());
            nodeMap.get(END_ID).setTriggerMode(AUTO);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("end node jober can not be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点EndNodeEventNotEmpty校验成功")
        public void testValidateEndNodeEventNotEmptySuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            nodeMap.get(END_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("end node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点EndNodeJobber校验成功")
        public void testValidateEndNodeJobberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            FlowEchoJober flowFitableJobber = new FlowEchoJober();
            nodeMap.get(END_ID).setJober(flowFitableJobber);
            flowDefinition.setNodeMap(nodeMap);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            assertEquals(errorMessage("end node jober can not be null"), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("流程事件校验规则测试集合")
    class EventsRuleTest {
        private final Parser parser = new FlowParser(null);

        private Validator flowValidator;

        private FlowDefinition flowDefinition;

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(getFilePath("general_flows_with_fitable_and_manual_task.json"));
            flowDefinition = parser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @BeforeEach
        void setUp() {
            List<FlowRule> flowRules = new ArrayList<>();
            flowRules.add(new EventsRule());
            flowValidator = new FlowValidator(flowRules);
            flowDefinition = getFlowDefinitionFromJson();
        }

        @Test
        @DisplayName("测试流程事件MetaId校验成功")
        public void testValidateFlowEventMetaIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setMetaId(EMPTY);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("node event metaId"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件FromMetaId校验成功")
        public void testValidateFlowEventFromMetaIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setFrom(EMPTY);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow event from metaId empty"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件FromMetaIdInvalid校验成功")
        public void testValidateFlowEventFromMetaIdInvalidSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setFrom("invalid");

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow event from metaId invalid"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件ToMetaId校验成功")
        public void testValidateFlowEventToMetaIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setTo(EMPTY);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow event to metaId empty"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件ToMetaIdInvalid校验成功")
        public void testValidateFlowEventToMetaIdInvalidSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setTo("invalid");

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow event to metaId invalid"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件fromEqualTo校验成功")
        public void testValidateFlowEventFromEqualToSuccess() {
            FlowEvent flowEvent = flowDefinition.getNodeMap().get("state1").getEvents().get(0);
            flowEvent.setTo(flowEvent.getFrom());

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow event from equal to metaId"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件StateNodeConditionRule校验成功")
        public void testValidateFlowEventStateNodeConditionRuleSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setConditionRule("flow event condition rule");

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("normal node condition rule"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件ConditionNodeConditionRule校验成功")
        public void testValidateFlowEventConditionNodeConditionRuleSuccess() {
            flowDefinition.getNodeMap().get("condi1").getEvents().get(0).setConditionRule(EMPTY);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("condition node condition rule"), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("流程手动任务校验规则测试集合")
    class JobersRuleTest {
        private final Parser flowParser = new FlowParser(null);

        private Validator flowValidator;

        private FlowDefinition flowDefinition;

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(getFilePath("general_flows_with_fitable_and_manual_task.json"));
            flowDefinition = flowParser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @BeforeEach
        void setUp() {
            List<FlowRule> flowRules = new ArrayList<>();
            flowRules.add(new JobersRule());
            flowValidator = new FlowValidator(flowRules);
            flowDefinition = getFlowDefinitionFromJson();
        }

        @Test
        @DisplayName("测试流程手动任务Type校验成功")
        public void testValidateFlowJobberTypeSuccess() {
            flowDefinition.getNodeMap().get("state2").getJober().setType(null);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow jober type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程手动任务Fitables校验成功")
        public void testValidateFlowJobberFitableSuccess() {
            flowDefinition.getNodeMap().get("state2").getJober().getFitables().add("codehub实现2");

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow jober fitables"), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("个性化节点类型解析测试")
    class CustomizedNodeTypeMatchingRuleTest {
        private final Parser flowParser = new FlowParser(null);

        private FlowDefinition flowDefinition;

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(
                    getFilePath("general_flows_with_customized_node_types_and_smart_form_task.json"));
            flowDefinition = flowParser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @Test
        @DisplayName("测试流程解析成功")
        public void testFlowParsingSuccess() {
            flowDefinition = getFlowDefinitionFromJson();
            assertEquals(START, flowDefinition.getFlowNode("start1").getType());
            assertEquals(STATE, flowDefinition.getFlowNode("state1").getType());
            assertEquals(END, flowDefinition.getFlowNode("ender1").getType());
            assertNull(flowDefinition.getFlowNode("event1"));
            assertNull(flowDefinition.getFlowNode("event2"));
            assertEquals(STATE, flowDefinition.getFlowNodeByEvent("event1").getType());
            assertEquals(END, flowDefinition.getFlowNodeByEvent("event2").getType());
        }
    }

    @Nested
    @DisplayName("流程智能表单人工任务校验规则测试集合")
    class SmartFormTaskRuleTest {
        private final Parser flowParser = new FlowParser(null);

        private Validator flowValidator;

        private FlowDefinition flowDefinition;

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(
                    getFilePath("general_flows_with_customized_node_types_and_smart_form_task.json"));
            flowDefinition = flowParser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @BeforeEach
        void setUp() {
            List<FlowRule> flowRules = new ArrayList<>();
            flowRules.add(new TasksRule(null));
            flowValidator = new FlowValidator(flowRules);
            flowDefinition = getFlowDefinitionFromJson();
        }

        @Test
        @DisplayName("测试智能表单任务type校验成功")
        public void testValidateSmartFormTaskTypeSuccess() {
            flowDefinition.getNodeMap().get("state1").getTask().setTaskType(null);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow task type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试智能表单任务ID校验成功")
        public void testValidateSmartFormTaskIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getTask().setTaskId(null);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow AIPP_SMART_FORM task id"), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("流程回调函数校验规则测试集合")
    class CallbacksRuleTest {
        private final Parser flowParser = new FlowParser(null);

        private Validator flowValidator;

        private FlowDefinition flowDefinition;

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData = getJsonData(getFilePath("general_flows_with_fitable_and_manual_task.json"));
            flowDefinition = flowParser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @BeforeEach
        void setUp() {
            List<FlowRule> flowRules = new ArrayList<>();
            flowRules.add(new CallbacksRule());
            flowValidator = new FlowValidator(flowRules);
            flowDefinition = getFlowDefinitionFromJson();
        }

        @Test
        @DisplayName("测试流程回调函数NodeType校验成功")
        public void testValidateFlowCallbackNodeTypeSuccess() {
            flowDefinition.getNodeMap().get("start1").setCallback(new FlowGeneralCallback());

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow callback node type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程回调函数Type校验成功")
        public void testValidateFlowCallbackTypeSuccess() {
            flowDefinition.getNodeMap().get("state2").getCallback().setType(null);

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow callback type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程回调函数Fitables校验成功")
        public void testValidateFlowCallbackFitableSuccess() {
            flowDefinition.getNodeMap().get("state2").getCallback().getFitables().add("通知回调函数实现2");

            JobberParamException exception = assertThrows(JobberParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            assertEquals(errorMessage("flow callback fitables"), exception.getMessage());
        }
    }
}
