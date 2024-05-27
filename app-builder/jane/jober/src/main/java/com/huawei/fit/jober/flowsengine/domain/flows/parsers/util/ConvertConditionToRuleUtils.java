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
    private static final String CONDITION_KEY = "condition";

    private static final String VALUE_KEY = "value";

    private static final String CONDITIONS_KEY = "conditions";

    private static final String CONDITION_RELATION_KEY = "conditionRelation";

    private static final String FROM_KEY = "from";

    private static final String REFERENCE_FROM_TYPE = "Reference";

    private static final String INPUT_FROM_TYPE = "Input";

    private static final String UNDEFINED_EXPRESSION = "undefined";

    private static final String INPUT_TYPE_KEY = "type";

    private static final String VALUE_REFERENCE_NODE_KEY = "referenceNode";

    private static final String VALUE_VALUE_ARRAY_KEY = "value";

    private static final String BUSINESS_DATA_PREFIX = "businessData.";

    public static String convert(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONArray conditions = jsonObject.getJSONArray(CONDITIONS_KEY);
        String conditionRelation = OperatorInfo.getByCode(jsonObject.getString(CONDITION_RELATION_KEY)).getOperator();
        StringBuilder expressionBuilder = buildExpression(conditions, conditionRelation);

        return expressionBuilder.toString();
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
        OperatorInfo operator = OperatorInfo.getByCode(condition.getString(CONDITION_KEY));
        JSONArray values = condition.getJSONArray(VALUE_KEY);

        String leftExpression = generateLeftExpression(values, operator);
        String rightExpression = generateRightExpression(values, operator);

        return "(" + leftExpression + operator.getOperator() + rightExpression + ")";
    }

    private static String generateLeftExpression(JSONArray values, OperatorInfo operator) {
        if (OperatorInfo.UNARY_TYPE.equalsIgnoreCase(operator.getType()) || OperatorInfo.BINARY_TYPE.equalsIgnoreCase(
                operator.getType())) {
            JSONObject left = values.getJSONObject(0);
            return generateExpression(left);
        }
        return ""; // Unary operators do not require a right expression
    }

    private static String generateRightExpression(JSONArray values, OperatorInfo operator) {
        if (OperatorInfo.BINARY_TYPE.equalsIgnoreCase(operator.getType())) {
            JSONObject right = values.getJSONObject(1);
            return generateExpression(right);
        }
        return ""; // Unary operators do not require a right expression
    }

    private static String generateExpression(JSONObject valueObj) {
        String from = valueObj.getString(FROM_KEY);
        if (REFERENCE_FROM_TYPE.equals(from)) {
            return buildReferenceExpression(valueObj);
        } else if (INPUT_FROM_TYPE.equals(from)) {
            return formatInputValue(valueObj);
        }
        return UNDEFINED_EXPRESSION;
    }

    private static String buildReferenceExpression(JSONObject valueObj) {
        String referenceNode = valueObj.getString(VALUE_REFERENCE_NODE_KEY);
        JSONArray valueArray = valueObj.getJSONArray(VALUE_VALUE_ARRAY_KEY);
        StringBuilder valueBuilder = new StringBuilder(BUSINESS_DATA_PREFIX + Constant.BUSINESS_DATA_INTERNAL_KEY + "."
                + Constant.INTERNAL_OUTPUT_SCOPE_KEY + ".").append(referenceNode);
        for (Object val : valueArray) {
            valueBuilder.append(".").append(val.toString());
        }
        return valueBuilder.toString();
    }

    private static String formatInputValue(JSONObject inputObj) {
        String type = inputObj.getString(INPUT_TYPE_KEY);
        switch (type) {
            case "Boolean":
            case "Number":
            case "Integer":
                return inputObj.getString("value");
            case "String":
                return "\"" + inputObj.getString("value") + "\"";
            default:
                return "undefined";
        }
    }
}
