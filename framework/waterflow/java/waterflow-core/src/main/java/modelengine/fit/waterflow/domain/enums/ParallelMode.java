/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.enums;

import static modelengine.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;

import modelengine.fit.waterflow.common.exceptions.WaterflowParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 并行节点的操作类型
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
public enum ParallelMode {
    ALL("all"),
    EITHER("either"),
    ;

    private final String code;

    ParallelMode(String code) {
        this.code = code;
    }

    /**
     * parseFrom
     *
     * @param code code
     * @return ParallelMode
     */
    public static ParallelMode parseFrom(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "ParallelMode", code));
    }
}
