/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.definitions.nodes.events;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.huawei.fit.waterflow.domain.context.FlowData;

import com.googlecode.aviator.AviatorEvaluator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程实践测试类，测试实践的条件规则转换正确性
 *
 * @author g00564732
 * @since 1.0
 */
class FlowEventTest {
    @Nested
    @DisplayName("流程事件条件规则转换测试集合")
    class FlowEventConditionRuleTest {
        @Test
        @DisplayName("流程事件条件规则bool测试")
        void testFlowEventWithBoolConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "true");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            FlowEvent flowEvent = FlowEvent.builder().conditionRule("{{test}}").build();

            String executableRule = flowEvent.getExecutableRule(flowData);

            assertTrue((boolean) AviatorEvaluator.execute(executableRule));
        }

        @Test
        @DisplayName("流程事件条件规则and测试")
        void testFlowEventWithAndConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "true");
            businessData.put("test1", "false");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            FlowEvent flowEvent = FlowEvent.builder().conditionRule("{{test}} && {{test1}}").build();

            String executableRule = flowEvent.getExecutableRule(flowData);

            assertFalse((boolean) AviatorEvaluator.execute(executableRule));
        }

        @Test
        @DisplayName("流程事件条件规则or测试")
        void testFlowEventWithOrConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "true");
            businessData.put("test1", "false");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            FlowEvent flowEvent = FlowEvent.builder().conditionRule("{{test}} || {{test1}}").build();

            String executableRule = flowEvent.getExecutableRule(flowData);

            assertTrue((boolean) AviatorEvaluator.execute(executableRule));
        }

        @Test
        @DisplayName("流程事件条件规则括号测试")
        void testFlowEventWithBracketsConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "true");
            businessData.put("test1", "false");
            businessData.put("test2", "false");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            FlowEvent flowEvent = FlowEvent.builder().conditionRule("{{test}} && !({{test1}} || {{test2}})").build();

            String executableRule = flowEvent.getExecutableRule(flowData);

            assertTrue((boolean) AviatorEvaluator.execute(executableRule));
        }

        @Test
        @DisplayName("流程事件条件规则括号测试")
        void testFlowEventWithBracketsAgainConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "true");
            businessData.put("test1", "false");
            businessData.put("test2", "true");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            FlowEvent flowEvent = FlowEvent.builder().conditionRule("{{test}} && !({{test1}} || {{test2}})").build();

            String executableRule = flowEvent.getExecutableRule(flowData);

            assertFalse((boolean) AviatorEvaluator.execute(executableRule));
        }

        @Test
        @DisplayName("流程事件条件规则计算测试")
        void testFlowEventWithComputeConditionRule() {
            Map<String, Object> businessData = new HashMap<>();
            businessData.put("test", "6");
            businessData.put("test1", "4");
            businessData.put("test2", "7");
            FlowData flowData = FlowData.builder().businessData(businessData).build();
            FlowEvent flowEvent = FlowEvent.builder()
                    .conditionRule("{{test}} > 5 && !({{test1}} < 3 || {{test2}} > 6)")
                    .build();

            String executableRule = flowEvent.getExecutableRule(flowData);

            assertFalse((boolean) AviatorEvaluator.execute(executableRule));
        }
    }
}