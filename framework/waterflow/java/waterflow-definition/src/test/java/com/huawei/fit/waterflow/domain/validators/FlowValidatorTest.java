/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.validators;

import static com.huawei.fit.waterflow.common.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeTriggerMode.AUTO;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeTriggerMode.MANUAL;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.END;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.START;
import static com.huawei.fit.waterflow.domain.enums.FlowNodeType.STATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fit.waterflow.FlowsDataBaseTest;
import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.definitions.FlowDefinition;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowEndNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.FlowStartNode;
import com.huawei.fit.waterflow.domain.definitions.nodes.callbacks.FlowGeneralCallback;
import com.huawei.fit.waterflow.domain.definitions.nodes.events.FlowEvent;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowEchoJober;
import com.huawei.fit.waterflow.domain.definitions.nodes.jobers.FlowGeneralJober;
import com.huawei.fit.waterflow.domain.parsers.FlowParser;
import com.huawei.fit.waterflow.domain.parsers.Parser;
import com.huawei.fit.waterflow.domain.validators.rules.CallbacksRule;
import com.huawei.fit.waterflow.domain.validators.rules.DefinitionRule;
import com.huawei.fit.waterflow.domain.validators.rules.EventsRule;
import com.huawei.fit.waterflow.domain.validators.rules.FlowRule;
import com.huawei.fit.waterflow.domain.validators.rules.JobersRule;
import com.huawei.fit.waterflow.domain.validators.rules.NodesRule;
import com.huawei.fit.waterflow.domain.validators.rules.TasksRule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
 * @author 高诗意
 * @since 1.0
 */
class FlowValidatorTest extends FlowsDataBaseTest {
    private static final String OTHER_STATE_ID = "state2";

    private static final String THIRD_STATE_ID = "state3";

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
        void testValidateSuccess() {
            Assertions.assertDoesNotThrow(() -> flowValidator.validate(flowDefinition));
        }

        @Test
        @DisplayName("测试流程定义flowContext配置校验失败")
        void testValidateFail() {
            HashMap<String, String> newProperties = new HashMap<>();
            newProperties.put("flowContext", "{{}}");
            flowDefinition.getFlowNode(STATE_ID).setProperties(newProperties);

            WaterflowParamException nullException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flowContext has been config, but the output key is empty"),
                    nullException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义MetaId校验成功")
        void testValidateDefinitionMetaIdNotNullSuccess() {
            flowDefinition.setMetaId(EMPTY);
            WaterflowParamException nullException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition metaId, metaId can not be blank"),
                    nullException.getMessage());

            flowDefinition.setMetaId("11");
            WaterflowParamException lengthException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition metaId, metaId length must be 32"),
                    lengthException.getMessage());

            flowDefinition.setMetaId("apimckapimckapimckapimckapimcka!");
            WaterflowParamException specialCharException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition metaId, metaId contains special characters"),
                    specialCharException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Name校验成功")
        void testValidateDefinitionNameSuccess() {
            flowDefinition.setName(EMPTY);
            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition name, name can not be blank"),
                    exception.getMessage());

            flowDefinition.setName("apimckapimckapimckapimckapimckapapimckapimckapimckapimckapimckapap"
                    + "imckapimckapimckapimckapimckapapimckapimckapimckapimckapimcka"
                    + "papimckapimckapimckapimckapimckapapimckapimckapimckapimckapim"
                    + "ckapapimckapimckapimckapimckapimckapapimckapimckapimckapimckapimckap1");
            WaterflowParamException lengthException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition name, name length over 256"),
                    lengthException.getMessage());

            flowDefinition.setName("name@");
            WaterflowParamException specialCharException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition name, name contains special characters"),
                    specialCharException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Version校验成功")
        void testValidateDefinitionVersionSuccess() {
            flowDefinition.setVersion(EMPTY);
            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition version, version can not be blank"),
                    exception.getMessage());

            flowDefinition.setVersion("1");
            WaterflowParamException formatException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition version, version format must be X.Y.Z"),
                    formatException.getMessage());

            flowDefinition.setVersion("1.0.str");
            WaterflowParamException stringException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition version, version format must be X.Y.Z"),
                    stringException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Tenant校验成功")
        void testValidateDefinitionTenantSuccess() {
            flowDefinition.setTenant(EMPTY);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition tenant, tenant can not be blank"),
                    exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义status校验成功")
        void testValidateDefinitionStatusSuccess() {
            flowDefinition.setStatus(null);
            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition status, status can not be blank"),
                    exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义Nodes校验成功")
        void testValidateDefinitionNodesSuccess() {
            flowDefinition.setNodeMap(new HashMap<>());

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition nodes, nodes can not be empty"),
                    exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义NodeSize校验成功")
        void testValidateDefinitionNodeSizeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.remove(CONDITION_ID);
            nodeMap.remove(STATE_ID);
            nodeMap.remove(OTHER_STATE_ID);
            nodeMap.remove(THIRD_STATE_ID);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("all node number, node number must more than 3"),
                    exception.getMessage());

            flowDefinition.setNodeMap(new HashMap<>());
            WaterflowParamException nullException = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow definition nodes, nodes can not be empty"),
                    nullException.getMessage());
        }

        @Test
        @DisplayName("测试流程定义StartNodeSize校验成功")
        void testValidateDefinitionStartNodeSizeLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.remove(START_ID);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node number"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义StartNodeSize校验成功")
        void testValidateDefinitionStartNodeSizeGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.put("temp", getStartNode());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node number"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义EndNodeSize校验成功")
        void testValidateDefinitionEndNodeSizeLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.remove(END_ID);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("end node number"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程定义EndNodeSize校验成功")
        void testValidateDefinitionEndNodeSizeGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.put("temp", getEndNode());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("end node number"), exception.getMessage());
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
        void testValidateNodeMetaIdSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setMetaId(EMPTY);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow node metaId, metaId can not be blank"), exception.getMessage());

            nodeMap.get(START_ID).setMetaId("start11");
            exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow node metaId size must be 6"), exception.getMessage());

            nodeMap.get(START_ID).setMetaId("start%");
            exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow node metaId not allow special char"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点Name校验成功")
        void testValidateNodeNameSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(CONDITION_ID).setName(EMPTY);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow node name, name can not be blank"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点Type校验成功")
        void testValidateNodeVersionSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(END_ID).setType(null);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow node type, node type can not be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点TriggerMode校验成功")
        void testValidateNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).setTriggerMode(null);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow node trigger mode, can not be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点手动任务id校验成功")
        void testValidateNodeTaskIdSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).getTask().setTaskId(null);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow APPROVING_TASK task id"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点手动任务类型校验成功")
        void testValidateNodeTaskTypeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).getTask().setTaskType(null);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow task type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点手动任务属性校验成功")
        void testValidateNodeTaskPropertiesSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).getTask().getProperties().remove("title");
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow task title"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点自动任务类型校验成功")
        void testValidateNodeJoberTypeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state2").getJober().setType(null);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow jober type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点自动任务fitables校验成功")
        void testValidateNodeJoberFitablesSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state2").getJober().setFitables(null);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow jober fitables"), exception.getMessage());

            nodeMap.get("state2").getJober().setFitables(new HashSet<>());
            flowDefinition.setNodeMap(nodeMap);

            exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow jober fitables"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点自动任务HttpJober校验成功")
        void testValidateNodeHttpJoberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state3").getJober().setProperties(new HashMap<>());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow http jober entity"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点过滤器校验成功")
        void testValidateNodeFilterSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get("state1").getTaskFilter().getProperties().put("threshold", "a");
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow filter threshold"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeTriggerMode校验成功")
        void testValidateStartNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setTriggerMode(MANUAL);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node trigger mode"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeTriggerMode校验成功")
        void testValidateStartNodeJoberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setTriggerMode(AUTO);
            nodeMap.get(START_ID).setJober(new FlowEchoJober());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node jober should be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeEventLessThan1校验成功")
        void testValidateStartNodeEventLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(START_ID).setEvents(new ArrayList<>());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeEventGreatThan1校验成功")
        void testValidateStartNodeEventGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            events.add(new FlowEvent());
            nodeMap.get(START_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StartNodeJobber校验成功")
        void testValidateStartNodeJobberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            FlowEchoJober flowFitableJobber = new FlowEchoJober();
            nodeMap.get(START_ID).setJober(flowFitableJobber);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("start node jober should be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StateNodeEventLessThan1校验成功")
        void testValidateStateNodeEventLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(STATE_ID).setEvents(new ArrayList<>());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("state node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StateNodeEventGreatThan1校验成功")
        void testValidateStateNodeEventGreatThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            events.add(new FlowEvent());
            nodeMap.get(STATE_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("state node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点StateNodeJobber校验成功")
        void testValidateStateNodeJobberSuccess() {
            Assertions.assertDoesNotThrow(() -> flowNodeValidator.validate(flowDefinition));
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeTriggerMode校验成功")
        void testValidateConditionNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(CONDITION_ID).setTriggerMode(MANUAL);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("condition node trigger mode"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeEventLessThan1校验成功")
        void testValidateConditionNodeEventLessThan1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(CONDITION_ID).setEvents(new ArrayList<>());
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("condition node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeEventEqual1校验成功")
        void testValidateConditionNodeEventEqual1Success() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            nodeMap.get(CONDITION_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("condition node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点ConditionNodeJobber校验成功")
        void testValidateConditionNodeJobberSuccess() {
            Assertions.assertDoesNotThrow(() -> flowNodeValidator.validate(flowDefinition));
        }

        @Test
        @DisplayName("测试流程节点EndNodeTriggerMode校验成功")
        void testValidateEndNodeTriggerModeSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(END_ID).setTriggerMode(MANUAL);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("end node trigger mode"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点EndNode自动任务校验成功")
        void testValidateEndNodeJoberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            nodeMap.get(END_ID).setJober(new FlowGeneralJober());
            nodeMap.get(END_ID).setTriggerMode(AUTO);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("end node jober can not be null"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点EndNodeEventNotEmpty校验成功")
        void testValidateEndNodeEventNotEmptySuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            List<FlowEvent> events = new ArrayList<>();
            events.add(new FlowEvent());
            nodeMap.get(END_ID).setEvents(events);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("end node event size"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程节点EndNodeJobber校验成功")
        void testValidateEndNodeJobberSuccess() {
            Map<String, FlowNode> nodeMap = flowDefinition.getNodeMap();
            FlowEchoJober flowFitableJobber = new FlowEchoJober();
            nodeMap.get(END_ID).setJober(flowFitableJobber);
            flowDefinition.setNodeMap(nodeMap);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowNodeValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("end node jober can not be null"), exception.getMessage());
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
        void testValidateFlowEventMetaIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setMetaId(EMPTY);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("node event metaId"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件FromMetaId校验成功")
        void testValidateFlowEventFromMetaIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setFrom(EMPTY);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow event from metaId empty"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件FromMetaIdInvalid校验成功")
        void testValidateFlowEventFromMetaIdInvalidSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setFrom("invalid");

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow event from metaId invalid"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件ToMetaId校验成功")
        void testValidateFlowEventToMetaIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setTo(EMPTY);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow event to metaId empty"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件ToMetaIdInvalid校验成功")
        void testValidateFlowEventToMetaIdInvalidSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setTo("invalid");

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow event to metaId invalid"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件fromEqualTo校验成功")
        void testValidateFlowEventFromEqualToSuccess() {
            FlowEvent flowEvent = flowDefinition.getNodeMap().get("state1").getEvents().get(0);
            flowEvent.setTo(flowEvent.getFrom());

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow event from equal to metaId"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件StateNodeConditionRule校验成功")
        void testValidateFlowEventStateNodeConditionRuleSuccess() {
            flowDefinition.getNodeMap().get("state1").getEvents().get(0).setConditionRule("flow event condition rule");

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("normal node condition rule"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程事件ConditionNodeConditionRule校验成功")
        void testValidateFlowEventConditionNodeConditionRuleSuccess() {
            flowDefinition.getNodeMap().get("condi1").getEvents().get(0).setConditionRule(EMPTY);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("condition node condition rule"), exception.getMessage());
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
        void testValidateFlowJobberTypeSuccess() {
            flowDefinition.getNodeMap().get("state2").getJober().setType(null);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow jober type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程手动任务Fitables校验成功")
        void testValidateFlowJobberFitableSuccess() {
            flowDefinition.getNodeMap().get("state2").getJober().getFitables().add("codehub实现2");

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow jober fitables"), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("个性化节点类型解析测试")
    class CustomizedNodeTypeMatchingRuleTest {
        private final Parser flowParser = new FlowParser(null);

        private FlowDefinition flowDefinition;

        private FlowDefinition getFlowDefinitionFromJson() {
            String jsonData =
                    getJsonData(getFilePath("general_flows_with_customized_node_types_and_smart_form_task.json"));
            flowDefinition = flowParser.parse(jsonData);
            flowDefinition.setTenant(TENANT);
            return flowDefinition;
        }

        @Test
        @DisplayName("测试流程解析成功")
        void testFlowParsingSuccess() {
            flowDefinition = getFlowDefinitionFromJson();
            assertEquals(START, flowDefinition.getFlowNode("start1").getType());
            assertEquals(STATE, flowDefinition.getFlowNode("state1").getType());
            assertEquals(END, flowDefinition.getFlowNode("ender1").getType());
            Assertions.assertNull(flowDefinition.getFlowNode("event1"));
            Assertions.assertNull(flowDefinition.getFlowNode("event2"));
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
            String jsonData =
                    getJsonData(getFilePath("general_flows_with_customized_node_types_and_smart_form_task.json"));
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
        void testValidateSmartFormTaskTypeSuccess() {
            flowDefinition.getNodeMap().get("state1").getTask().setTaskType(null);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow task type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试智能表单任务ID校验成功")
        void testValidateSmartFormTaskIdSuccess() {
            flowDefinition.getNodeMap().get("state1").getTask().setTaskId(null);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow AIPP_SMART_FORM task id"), exception.getMessage());
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
        void testValidateFlowCallbackNodeTypeSuccess() {
            flowDefinition.getNodeMap().get("start1").setCallback(new FlowGeneralCallback());

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow callback node type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程回调函数Type校验成功")
        void testValidateFlowCallbackTypeSuccess() {
            flowDefinition.getNodeMap().get("state2").getCallback().setType(null);

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow callback type"), exception.getMessage());
        }

        @Test
        @DisplayName("测试流程回调函数Fitables校验成功")
        void testValidateFlowCallbackFitableSuccess() {
            flowDefinition.getNodeMap().get("state2").getCallback().getFitables().add("通知回调函数实现2");

            WaterflowParamException exception = Assertions.assertThrows(WaterflowParamException.class,
                    () -> flowValidator.validate(flowDefinition));
            Assertions.assertEquals(errorMessage("flow callback fitables"), exception.getMessage());
        }
    }
}
