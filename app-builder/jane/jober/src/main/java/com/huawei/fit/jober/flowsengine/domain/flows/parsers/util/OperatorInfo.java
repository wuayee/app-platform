/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.util;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.common.exceptions.JobberException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运算符相关信息
 *
 * @author 陈镕希 c00572808
 * @since 2024-05-15
 */
@Getter
@AllArgsConstructor
public enum OperatorInfo {
    NOT_EQUAL(OperatorInfo.BINARY_TYPE, " != ", false),
    EQUAL(OperatorInfo.BINARY_TYPE, " == ", false),
    IS_TRUE(OperatorInfo.UNARY_TYPE, "", false),
    IS_FALSE(OperatorInfo.UNARY_TYPE, " == false", false),
    IS_EMPTY(OperatorInfo.UNARY_TYPE, ".is_empty()", false),
    IS_NOT_EMPTY(OperatorInfo.UNARY_TYPE, ".is_empty()", true),
    GREATER_THAN(OperatorInfo.BINARY_TYPE, " > ", false),
    GREATER_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, " >= ", false),
    LESS_THAN(OperatorInfo.BINARY_TYPE, " < ", false),
    LESS_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, " <= ", false),
    AND(OperatorInfo.BINARY_TYPE, " && ", false),
    OR(OperatorInfo.BINARY_TYPE, " || ", false),
    TRUE(OperatorInfo.NULLARY_TYPE, "true", false),
    FALSE(OperatorInfo.NULLARY_TYPE, "false", false),
    IS_NULL(OperatorInfo.UNARY_TYPE, " == null", false),
    IS_NOT_NULL(OperatorInfo.UNARY_TYPE, " != null", false),
    IS_EMPTY_STRING(OperatorInfo.UNARY_TYPE, ".trim().is_empty()", false),
    IS_NOT_EMPTY_STRING(OperatorInfo.UNARY_TYPE, ".trim().is_empty()", true),
    ;

    /**
     * 表示零目运算符的类型常量
     */
    public static final String NULLARY_TYPE = "nullary";

    /**
     * 表示一目运算符的类型常量
     */
    public static final String UNARY_TYPE = "unary";

    /**
     * 表示二目运算符的类型常量
     */
    public static final String BINARY_TYPE = "binary";

    private static final String VALUE_KEY = "value";

    private static final String FROM_KEY = "from";

    private static final String REFERENCE_FROM_TYPE = "Reference";

    private static final String INPUT_FROM_TYPE = "Input";

    private static final String UNDEFINED_EXPRESSION = "undefined";

    private static final String INPUT_TYPE_KEY = "type";

    private static final String VALUE_REFERENCE_NODE_KEY = "referenceNode";

    private static final String VALUE_VALUE_ARRAY_KEY = "value";

    private static final String BUSINESS_DATA_PREFIX = "businessData.";

    private final String type;

    private final String operator;

    private final boolean isInvert;

    /**
     * 根据code获得对应运算符枚举类的 {@link OperatorInfo}。
     *
     * @param code 枚举类名称对应code的 {@link String}。
     * @return code对应运算符枚举类的 {@link OperatorInfo}。
     */
    public static OperatorInfo getByCode(String code) {
        for (OperatorInfo op : values()) {
            if (op.name().equalsIgnoreCase(code.replace(" ", "_"))) {
                return op;
            }
        }
        throw new JobberException(INPUT_PARAM_IS_INVALID, "OperatorInfo code");
    }

    /**
     * 根据枚举类中isInvert的值返回不同的运算符结果。
     *
     * @return 根据枚举类中isInvert的值返回不同的运算符结果的 {@link String}。
     */
    private String getInvert() {
        return isInvert ? "!" : "";
    }

    /**
     * 根据条件转换出对应的表达式。
     *
     * @param condition 表示条件对象的 {@link JSONObject}。
     * @return 表示根据条件转换出的表达式的 {@link String}。
     */
    public String buildConditionExpression(JSONObject condition) {
        JSONArray values = condition.getJSONArray(VALUE_KEY);
        String leftExpression = generateLeftExpression(values, this);
        String rightExpression = generateRightExpression(values, this);
        return "(" + this.getInvert() + leftExpression + this.getOperator() + rightExpression + ")";
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
        } else {
            return UNDEFINED_EXPRESSION;
        }
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
        String inputType = inputObj.getString(INPUT_TYPE_KEY);
        switch (inputType) {
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