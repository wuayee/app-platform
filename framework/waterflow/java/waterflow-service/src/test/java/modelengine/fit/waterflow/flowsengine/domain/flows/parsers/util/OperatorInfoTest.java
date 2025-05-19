/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import modelengine.fit.waterflow.MethodNameLoggerExtension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * {@link OperatorInfo}对应测试类
 *
 * @author 陈镕希
 * @since 2024-08-06
 */
@ExtendWith(MethodNameLoggerExtension.class)
class OperatorInfoTest {
    @ParameterizedTest
    @MethodSource("provideConditionParameters")
    @DisplayName("构造条件表达式")
    void buildConditionExpression(String condition, String rightType, String rightValue, String expectedExpression) {
        ConditionObject conditionObject = new ConditionObject(condition, "7efa08b7-dbb6-4484-a0f1-de1595b3898d",
                Arrays.asList(createReferenceObject(ReferenceParams.builder()
                        .name("left")
                        .from("Reference")
                        .id("c726905b-b90d-40d9-864f-96acc4570884")
                        .type("String")
                        .referenceId("input_493c631d-bf0e-445a-b7ea-ba9f163f4200")
                        .referenceKey("Question")
                        .build()), createInputObject(
                        new InputParams("right", "Input", "93a884af-9b34-4da9-a2bf-d08b6be6b14f", rightType,
                                rightValue))));

        JSONObject jsonObject = conditionObject.toJsonObject();
        OperatorInfo operatorInfo = OperatorInfo.getByCode(jsonObject.getString("condition"));

        assertEquals(expectedExpression, operatorInfo.buildConditionExpression(jsonObject));
    }

    private static Stream<Arguments> provideConditionParameters() {
        return Stream.of(Arguments.of("contains", "String", "cr",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".contains(\"cr\"))"),
                Arguments.of("does not contain", "String", "cr",
                        "(!businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".contains(\"cr\"))"),
                Arguments.of("longer than", "Integer", "4",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".len() > 4)"),
                Arguments.of("longer than or equal", "Integer", "4",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".len() >= 4)"),
                Arguments.of("shorter than", "Integer", "4",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".len() < 4)"),
                Arguments.of("shorter than or equal", "Integer", "4",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".len() <= 4)"),
                Arguments.of("starts with", "String", "cba",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".starts_with(\"cba\"))"),
                Arguments.of("ends with", "String", "cba",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + ".ends_with(\"cba\"))"),
                Arguments.of("equal", "String", "\"\"",
                        "(businessData.get(\"_internal\").get(\"outputScope\").get(\"null\").get(\"Question\").trim()"
                                + " == \"\\\"\\\"\")"));
    }

    private JSONObject createReferenceObject(ReferenceParams params) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", params.name);
        jsonObject.put("from", params.from);
        jsonObject.put("id", params.id);
        jsonObject.put("type", params.type);
        JSONArray valuesArray = new JSONArray();
        valuesArray.add(params.referenceKey);
        jsonObject.put("value", valuesArray);
        jsonObject.put("referenceId", params.referenceId);
        jsonObject.put("referenceKey", params.referenceKey);
        return jsonObject;
    }

    private JSONObject createInputObject(InputParams params) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", params.name);
        jsonObject.put("from", params.from);
        jsonObject.put("id", params.id);
        jsonObject.put("type", params.type);
        jsonObject.put("value", params.value);
        return jsonObject;
    }

    private static class ConditionObject {
        private final String condition;

        private final String id;

        private final List<JSONObject> values;

        ConditionObject(String condition, String id, List<JSONObject> values) {
            this.condition = condition;
            this.id = id;
            this.values = values;
        }

        JSONObject toJsonObject() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("condition", condition);
            jsonObject.put("id", id);
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(values);
            jsonObject.put("value", jsonArray);
            return jsonObject;
        }
    }

    @Getter
    @Setter
    @Builder
    private static class ReferenceParams {
        final String name;

        final String from;

        final String id;

        final String type;

        final String referenceId;

        final String referenceKey;
    }

    private static class InputParams {
        final String name;

        final String from;

        final String id;

        final String type;

        final String value;

        InputParams(String name, String from, String id, String type, String value) {
            this.name = name;
            this.from = from;
            this.id = id;
            this.type = type;
            this.value = value;
        }
    }
}