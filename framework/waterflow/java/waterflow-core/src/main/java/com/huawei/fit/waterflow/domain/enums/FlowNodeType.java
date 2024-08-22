/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.enums;

import static com.huawei.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;
import static java.util.Locale.ROOT;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 流程定义节点类型
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public enum FlowNodeType {
    START("START", false),
    STATE("STATE", false),
    CONDITION("CONDITION", false),
    PARALLEL("PARALLEL", false),
    FORK("FORK", true),
    JOIN("JOIN", true),
    EVENT("EVENT", true),
    END("END", false);

    private final String code;

    private final boolean subNode;

    FlowNodeType(String code, boolean subNode) {
        this.code = code;
        this.subNode = subNode;
    }

    /**
     * getNodeType
     *
     * @param code code
     * @return FlowNodeType
     */
    public static FlowNodeType getNodeType(String code) {
        return Arrays.stream(values())
                .filter(value -> code.toUpperCase(ROOT).endsWith(value.getCode()))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowNodeType", code));
    }
}
