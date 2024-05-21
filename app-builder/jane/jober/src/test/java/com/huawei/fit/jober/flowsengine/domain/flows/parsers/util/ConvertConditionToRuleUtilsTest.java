/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * ConvertConditionToRuleUtils对应测试类
 *
 * @author 陈镕希 c00572808
 * @since 2024-05-13
 */
@Disabled
class ConvertConditionToRuleUtilsTest {
    @Test
    public void testEqualConditionFromInput() {
        String jsonInput = "{\"conditions\": [{\"condition\": \"equal\", \"value\": [{\"from\": \"Input\", \"type\": \"Number\", \"value\": \"1\"}, {\"from\": \"Input\", \"type\": \"Number\", \"value\": \"2\"}] }], \"conditionRelation\": \"and\"}";
        String expectedOutput = "(1 == 2)";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }

    @Test
    public void testNotEqualConditionFromReference() {
        String jsonInput = "{\"conditions\": [{\"condition\": \"not equal\", \"value\": [{\"from\": \"Reference\", \"referenceNode\": \"node1\", \"type\": \"String\", \"value\": [\"value1\"]}, {\"from\": \"Reference\", \"referenceNode\": \"node2\", \"type\": \"String\", \"value\": [\"value2\"]}] }], \"conditionRelation\": \"and\"}";
        String expectedOutput = "(businessData.nodeData.node1.value1 != businessData.nodeData.node2.value2)";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }

    @Test
    public void testIsTrueCondition() {
        String jsonInput = "{\"conditions\": [{\"condition\": \"is true\", \"value\": [{\"from\": \"Reference\", \"referenceNode\": \"node1\", \"type\": \"Boolean\", \"value\": [\"isActive\"]}]}], \"conditionRelation\": \"or\"}";
        String expectedOutput = "(businessData.nodeData.node1.isActive)";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }

    @Test
    public void testIsEmptyConditionFromReference() {
        String jsonInput = "{\"conditions\": [{\"condition\": \"is empty\", \"value\": [{\"from\": \"Reference\", \"referenceNode\": \"node1\", \"type\": \"String\", \"value\": [\"list\"]}]}], \"conditionRelation\": \"and\"}";
        String expectedOutput = "(businessData.nodeData.node1.list.isEmpty())";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }

    @Test
    public void testMixedConditionsAnd() {
        String jsonInput = "{\"conditions\": [{\"condition\": \"equal\", \"value\": [{\"from\": \"Input\", \"type\": \"Number\", \"value\": \"3\"}, {\"from\": \"Input\", \"type\": \"Number\", \"value\": \"3\"}]}, {\"condition\": \"not equal\", \"value\": [{\"from\": \"Input\", \"type\": \"String\", \"value\": \"hello\"}, {\"from\": \"Input\", \"type\": \"String\", \"value\": \"world\"}]}], \"conditionRelation\": \"and\"}";
        String expectedOutput = "(3 == 3) && (\"hello\" != \"world\")";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }

    @Test
    public void testMixedConditionsOr() {
        String jsonInput = "{\"conditions\": [{\"condition\": \"is true\", \"value\": [{\"from\": \"Reference\", \"referenceNode\": \"node1\", \"type\": \"Boolean\", \"value\": [\"flag\"]}]}, {\"condition\": \"is empty\", \"value\": [{\"from\": \"Reference\", \"referenceNode\": \"node2\", \"type\": \"String\", \"value\": [\"array\"]}]}], \"conditionRelation\": \"or\"}";
        String expectedOutput = "(businessData.nodeData.node1.flag) || (businessData.nodeData.node2.array.isEmpty())";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }

    @Test
    public void testBooleanInput() {
        String jsonInput = "{\"conditionRelation\": \"and\",\"conditions\": [{\"id\": \"411d1692-44c6-445c-96e7-a59945b139d1\",\"condition\": \"equal\",\"value\": [{\"id\": \"dbbb64a1-910b-473a-8239-51a882c9a580\",\"name\": \"left\",\"type\": \"Boolean\",\"from\": \"Reference\",\"value\": [\"222\"],\"referenceNode\": \"6qm5eg\",\"referenceKey\": \"222\",\"referenceId\": \"input_808057db-780f-4b0c-86cc-5888a5bbc7f5\"},{\"id\": \"20cce8a4-ca5d-410a-a0d2-4dc222e877e3\",\"name\": \"right\",\"type\": \"Boolean\",\"from\": \"Input\",\"value\": false,\"referenceNode\": \"\",\"referenceId\": \"\",\"referenceKey\": \"\"}]}]}";
        String expectedOutput = "(businessData.nodeData.6qm5eg.222 == false)";
        assertEquals(expectedOutput, ConvertConditionToRuleUtils.convert(jsonInput));
    }
}