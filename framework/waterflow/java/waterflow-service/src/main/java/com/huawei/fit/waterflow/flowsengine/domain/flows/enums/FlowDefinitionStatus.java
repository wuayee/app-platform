/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 流程定义对应状态
 *
 * @author 杨祥宇
 * @since 2023/9/25
 */
@Getter
public enum FlowDefinitionStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String code;

    FlowDefinitionStatus(String code) {
        this.code = code;
    }

    /**
     * getFlowDefinitionStatus
     *
     * @param code code
     * @return FlowDefinitionStatus
     */
    public static FlowDefinitionStatus getFlowDefinitionStatus(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "FlowDefinitionStatus", code));
    }
}
