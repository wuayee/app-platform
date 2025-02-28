/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jade.waterflow.ErrorCodes;
import modelengine.fit.jade.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.utils.OhScriptExecutor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * OhScript执行测试类
 *
 * @author 李哲峰
 * @since 2023/12/21
 */
class OhScriptExecutorTest {
    @Nested
    @DisplayName("流程条件节点条件规则转换测试集合")
    class FlowEventConditionRuleTest {
        @Test
        @DisplayName("流程条件节点条件规则bool测试")
        void testBoolConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", true);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点条件规则and测试")
        void testAndConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            Map<String, Object> passData = new HashMap<>();
            businessData.put("test", true);
            passData.put("test1", false);
            FlowData flowData = FlowData.builder().businessData(businessData).passData(passData).build();
            String conditionRule = "businessData.test && passData.test1";
            assertFalse(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点条件规则or测试")
        void testOrConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            Map<String, Object> passData = new HashMap<>();
            businessData.put("test", true);
            passData.put("test1", false);
            FlowData flowData = FlowData.builder().businessData(businessData).passData(passData).build();
            String conditionRule = "businessData.test || passData.test1";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点条件规则括号测试")
        void testBracketsConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", true);
            businessData.put("test1", false);
            businessData.put("test2", false);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test && !(businessData.test1 || businessData.test2)";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点条件规则括号测试")
        void testBracketsAgainConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", true);
            businessData.put("test1", false);
            businessData.put("test2", true);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test && !(businessData.test1 || businessData.test2)";
            assertFalse(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点条件规则计算测试")
        void testComputeConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", 6);
            businessData.put("test1", 4);
            businessData.put("test2", 7);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test > 5 && !(businessData.test1 < 3 || businessData.test2 > 6)";
            assertFalse(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点条件规则不符合OhScript语法测试")
        void testOhScriptGrammarError() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", true);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData..test";

            WaterflowException ex = assertThrows(WaterflowException.class,
                    () -> OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
            assertEquals(ErrorCodes.FLOW_ENGINE_OHSCRIPT_GRAMMAR_ERROR.getErrorCode(), ex.getCode());
        }

        @Test
        @Disabled
        @DisplayName("流程条件节点条件规则解析错误测试")
        void testConditionRuleParseError() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", true);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test.test1";

            WaterflowException ex = assertThrows(WaterflowException.class,
                    () -> OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
            assertEquals(ErrorCodes.FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("流程条件节点条件规则返回非布尔值测试")
        void testConditionRuleReturnNonBooleanValue() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", 1);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test";

            WaterflowException ex = assertThrows(WaterflowException.class,
                    () -> OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
            assertEquals(ErrorCodes.TYPE_CONVERT_FAILED.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("流程条件节点条件规则链式变量解析报错")
        void testConditionRuleChainedVariable() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test.test1", 1);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test.test1 == 1";
            WaterflowException ex = assertThrows(WaterflowException.class,
                    () -> OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
            assertEquals(ErrorCodes.FLOW_ENGINE_CONDITION_RULE_PARSE_ERROR.getErrorCode(), ex.getCode());
        }

        @Test
        @DisplayName("流程条件节点条件规则向前兼容测试")
        void testConditionRuleBackwardCompatibility() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "1");
            businessData.put("test1", "1");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test == businessData.test1";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程条件节点数组空比较测试")
        void testListEmpty() {
            Map<String, Object> businessData = new HashMap<>();
            ArrayList<Object> value = new ArrayList<>();
            businessData.put("test", value);
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test.isEmpty()";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));

            conditionRule = "!businessData.test.isEmpty()";
            assertFalse(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));

            value.add(1);
            flowData = FlowData.builder().businessData(businessData).build();
            conditionRule = "businessData.test.isEmpty()";
            assertFalse(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));

            conditionRule = "!businessData.test.isEmpty()";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }

        @Test
        @DisplayName("流程带引号的字符串比较测试")
        void testEmptyString() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "\"\"");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            String conditionRule = "businessData.test.trim() == \"\\\"\\\"\"";
            assertTrue(OhScriptExecutor.evaluateConditionRule(flowData, conditionRule));
        }
    }
}
