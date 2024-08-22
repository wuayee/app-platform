/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程引擎工具类测试
 *
 * @author 高诗意
 * @since 2023/11/21
 */
class FlowUtilTest {
    private static final Map<String, Object> BUSINESS_DATA = new HashMap<String, Object>() {
        {
            put("a", "testa");
            put("b", "testb");
            put("c", "testc");
        }
    };

    private static final Map<String, String> INPUT_PROPERTIES = new HashMap<String, String>() {
        {
            put("var1", "{{a}}, {{b}}, {{c}}");
            put("var2", "var1/{{a}}/var2/{{b}}/var3/{{c}}");
            put("var3", "var1/{{a}}/var2/{{n}}/var3/{{m}}");
            put("var4", "{{a}}");
            put("var5", "con1/con2/con3");
            put("var6", "");
            put("var7", null);
            put("var8", "var1/{{}}/var2/{{}}/var3/{{}}");
        }
    };

    @Test
    void shouldReturnValueWhenKeyPropertiesValueIsMultiVar() {
        assertEquals("testa, testb, testc", FlowUtil.replace(INPUT_PROPERTIES.get("var1"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnValueWhenKeyPropertiesValueIsUrlVar() {
        assertEquals("var1/testa/var2/testb/var3/testc", FlowUtil.replace(INPUT_PROPERTIES.get("var2"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnValueWhenKeyPropertiesValueIsUrlVarWithInvalidValue() {
        assertEquals("var1/testa/var2//var3/", FlowUtil.replace(INPUT_PROPERTIES.get("var3"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnValueWhenKeyPropertiesValueIsUrlVarWithEmptyValue() {
        assertEquals("var1/{{}}/var2/{{}}/var3/{{}}", FlowUtil.replace(INPUT_PROPERTIES.get("var8"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnValueWhenKeyPropertiesValueIsSingleVar() {
        assertEquals("testa", FlowUtil.replace(INPUT_PROPERTIES.get("var4"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnConstWhenKeyPropertiesValueIsConst() {
        assertEquals("con1/con2/con3", FlowUtil.replace(INPUT_PROPERTIES.get("var5"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnEmptyWhenKeyPropertiesValueIsEmpty() {
        assertEquals("", FlowUtil.replace(INPUT_PROPERTIES.get("var6"), BUSINESS_DATA));
    }

    @Test
    void shouldReturnNullWhenKeyPropertiesValueIsNull() {
        assertNull(FlowUtil.replace(INPUT_PROPERTIES.get("var7"), BUSINESS_DATA));
    }

    @Test
    void testFormatConditionRule() {
        assertEquals("businessData.var", FlowUtil.formatConditionRule("{{var}}"));
        assertEquals("businessData.var1 && businessData.var2", FlowUtil.formatConditionRule("{{var1}} && {{var2}}"));
        assertEquals("businessData.var1 == 1 || businessData.var2 > 2",
                FlowUtil.formatConditionRule("{{var1}} == 1 || {{var2}} > 2"));
        assertEquals("businessData.var == \"test\"", FlowUtil.formatConditionRule("{{var}} == 'test'"));
        assertEquals("businessData.var1 == \"test\"", FlowUtil.formatConditionRule("'{{var1}}' == 'test'"));
        assertEquals("businessData.isApproval && businessData.status == \"transferred\"",
                FlowUtil.formatConditionRule("{{isApproval}} && '{{status}}' == 'transferred'"));
    }

    @Test
    void shouldReturnOriginalVariableWhenInputMultiVar() {
        List<String> result = FlowUtil.originalVariable(INPUT_PROPERTIES.get("var1"));
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    void shouldReturnOriginalVariableWhenInputUrlVar() {
        List<String> result = FlowUtil.originalVariable(INPUT_PROPERTIES.get("var3"));
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("m"));
        assertTrue(result.contains("n"));
    }

    @Test
    void shouldReturnOriginalVariableWhenInputIsEmpty() {
        List<String> result = FlowUtil.originalVariable(INPUT_PROPERTIES.get("var6"));
        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnOriginalVariableWhenInputIsNull() {
        List<String> result = FlowUtil.originalVariable(INPUT_PROPERTIES.get("var7"));
        assertEquals(0, result.size());
    }

    @Test
    void shouldReturnMergedMapWhenTwoMapsHasNestMap() {
        List<String> lLevel1 = createLLevelData();
        List<String> lLevel2 = createLLevelData();
        Map<String, Object> level2 = new HashMap<String, Object>() {
            {
                put("level2", "level2");
                put("list1", lLevel2);
            }
        };
        Map<String, Object> level1 = createLevel1(lLevel1, level2);
        Map<String, Object> input = new HashMap<>();
        input.put("a", "a");
        input.put("level1", level1);
        List<String> tLlevel2 = new ArrayList<>();
        tLlevel2.add("list1");
        tLlevel2.add("list3");
        Map<String, Object> tLevel2 = createTLevel2(tLlevel2);
        Map<String, Object> tLevel1 = createTLevel1(tLevel2);
        Map<String, Object> target = new HashMap<String, Object>() {
            {
                put("a", "c");
                put("d", "d");
                put("level1", tLevel1);
            }
        };
        Map<String, Object> result = FlowUtil.mergeMaps(input, target);

        assertEquals("c", result.get("a"));
        assertEquals("d", result.get("d"));
        Map<String, Object> rLevel1 = ObjectUtils.cast(result.get("level1"));
        assertEquals("changed", rLevel1.get("level1"));
        assertEquals("addnew", rLevel1.get("addnew"));
        assertTrue(ObjectUtils.<List<String>>cast(rLevel1.get("list1")).contains("list1"));
        assertTrue(ObjectUtils.<List<String>>cast(rLevel1.get("list1")).contains("list2"));

        Map<String, Object> rLevel2 = ObjectUtils.cast(rLevel1.get("level2"));
        assertEquals("changed", rLevel2.get("level2"));
        assertEquals("newadd", rLevel2.get("newadd"));
        assertTrue(ObjectUtils.<List<String>>cast(rLevel2.get("list1")).contains("list1"));
        assertTrue(ObjectUtils.<List<String>>cast(rLevel2.get("list1")).contains("list3"));
        assertTrue(ObjectUtils.<List<String>>cast(rLevel2.get("list2")).contains("list1"));
        assertTrue(ObjectUtils.<List<String>>cast(rLevel2.get("list2")).contains("list3"));
    }

    private static Map<String, Object> createTLevel2(List<String> tLlevel2) {
        Map<String, Object> tLevel2 = new HashMap<String, Object>() {
            {
                put("level2", "changed");
                put("newadd", "newadd");
                put("list1", tLlevel2);
                put("list2", tLlevel2);
            }
        };
        return tLevel2;
    }

    private static Map<String, Object> createTLevel1(Map<String, Object> tLevel2) {
        Map<String, Object> tLevel1 = new HashMap<String, Object>() {
            {
                put("level1", "changed");
                put("addnew", "addnew");
                put("level2", tLevel2);
            }
        };
        return tLevel1;
    }

    private static HashMap<String, Object> createLevel1(List<String> lLevel1, Map<String, Object> level2) {
        return new HashMap<String, Object>() {
            {
                put("level1", "level1");
                put("list1", lLevel1);
                put("level2", level2);
            }
        };
    }

    private static ArrayList<String> createLLevelData() {
        return new ArrayList<String>() {
            {
                add("list1");
                add("list2");
            }
        };
    }
}
