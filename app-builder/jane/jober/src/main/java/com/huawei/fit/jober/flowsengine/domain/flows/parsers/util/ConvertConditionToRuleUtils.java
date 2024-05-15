/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.util;

import com.huawei.fit.jober.common.Constant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 转换条件为规则的工具类
 *
 * @author 陈镕希 c00572808
 * @since 2024-05-13
 */
public class ConvertConditionToRuleUtils {
    public static String convert(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONArray conditions = jsonObject.getJSONArray("conditions");
        String conditionRelation = getLogicalOperator(jsonObject.getString("conditionRelation"));
        StringBuilder expressionBuilder = buildExpression(conditions, conditionRelation);

        return expressionBuilder.toString();
    }


    private static String getLogicalOperator(String relation) {
        return "and".equals(relation) ? " && " : " || ";
    }

    private static StringBuilder buildExpression(JSONArray conditions, String conditionRelation) {
        StringBuilder expressionBuilder = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            if (i > 0) {
                expressionBuilder.append(conditionRelation);
            }
            expressionBuilder.append(buildIndividualCondition(conditions.getJSONObject(i)));
        }
        return expressionBuilder;
    }

    private static String buildIndividualCondition(JSONObject condition) {
        JSONArray values = condition.getJSONArray("value");
        JSONObject left = values.getJSONObject(0);
        OperatorInfo operator = getOperator(condition.getString("condition"));

        String leftExpression = generateExpression(left);
        String rightExpression = generateRightExpression(values, operator);

        return "(" + leftExpression + operator.operator + rightExpression + ")";
    }

    private static String generateRightExpression(JSONArray values, OperatorInfo operator) {
        if ("binary".equals(operator.type)) {
            JSONObject right = values.getJSONObject(1);
            return generateExpression(right);
        }
        return ""; // Unary operators do not require a right expression
    }


    private static class OperatorInfo {
        String type; // "unary" or "binary"
        String operator; // such as " == " or ".isEmpty()"

        public OperatorInfo(String type, String operator) {
            this.type = type;
            this.operator = operator;
        }
    }

    private static OperatorInfo getOperator(String condition) {
        switch (condition) {
            case "not equal":
                return new OperatorInfo("binary", " != ");
            case "is true":
                return new OperatorInfo("unary", ""); // Unary operator, no right operand needed
            case "is empty":
                return new OperatorInfo("unary", ".isEmpty()"); // Unary operator, applies to left operand
            case "is not empty":
                return new OperatorInfo("unary", ".isNotEmpty()"); // Unary operator, applies to left operand
            case "is false":
                return new OperatorInfo("unary", " == false"); // Unary operator, applies to left operand
            case "equal":
            default:
                return new OperatorInfo("binary", " == "); // Default binary equality check
        }
    }

    private static String generateExpression(JSONObject valueObj) {
        String from = valueObj.getString("from");
        if ("Reference".equals(from)) {
            return buildReferenceExpression(valueObj);
        } else if ("Input".equals(from)) {
            return formatInputValue(valueObj);
        }
        return "undefined";
    }

    private static String buildReferenceExpression(JSONObject valueObj) {
        String referenceNode = valueObj.getString("referenceNode");
        JSONArray valueArray = valueObj.getJSONArray("value");
        StringBuilder valueBuilder = new StringBuilder("businessData." + Constant.BUSINESS_DATA_INTERNAL_KEY + "."
                + Constant.INTERNAL_OUTPUT_SCOPE_KEY + ".").append(referenceNode);
        for (Object val : valueArray) {
            valueBuilder.append(".").append(val.toString());
        }
        return valueBuilder.toString();
    }

    private static String formatInputValue(JSONObject inputObj) {
        String type = inputObj.getString("type");
        switch (type) {
            case "Boolean":
            case "Number":
                return inputObj.getString("value");
            case "String":
                return "\"" + inputObj.getString("value") + "\"";
            default:
                return "undefined";
        }
    }
}
