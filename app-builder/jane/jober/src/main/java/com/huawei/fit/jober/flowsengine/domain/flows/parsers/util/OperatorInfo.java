/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.parsers.util;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.jober.common.exceptions.JobberException;

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
    NOT_EQUAL(OperatorInfo.BINARY_TYPE, " != "),
    EQUAL(OperatorInfo.BINARY_TYPE, " == "),
    IS_TRUE(OperatorInfo.UNARY_TYPE, ""),
    IS_FALSE(OperatorInfo.UNARY_TYPE, " == false"),
    IS_EMPTY(OperatorInfo.UNARY_TYPE, ".isEmpty()"),
    IS_NOT_EMPTY(OperatorInfo.UNARY_TYPE, ".isNotEmpty()"),
    GREATER_THAN(OperatorInfo.BINARY_TYPE, " > "),
    GREATER_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, " >= "),
    LESS_THAN(OperatorInfo.BINARY_TYPE, " < "),
    LESS_THAN_OR_EQUAL(OperatorInfo.BINARY_TYPE, " <= "),
    AND(OperatorInfo.BINARY_TYPE, " && "),
    OR(OperatorInfo.BINARY_TYPE, " || "),
    TRUE(OperatorInfo.NULLARY_TYPE, "true"),
    FALSE(OperatorInfo.NULLARY_TYPE, "false"),
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

    private final String type;

    private final String operator;

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
}