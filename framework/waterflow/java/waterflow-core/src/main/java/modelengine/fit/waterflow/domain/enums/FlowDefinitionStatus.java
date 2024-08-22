/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.enums;

import static modelengine.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;

import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 流程定义对应状态
 *
 * @author 杨祥宇
 * @since 1.0
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
     * 根据状态码获取definition的状态
     *
     * @param code 状态码
     * @return 状态
     */
    public static FlowDefinitionStatus getFlowDefinitionStatus(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowDefinitionStatus", code));
    }
}
