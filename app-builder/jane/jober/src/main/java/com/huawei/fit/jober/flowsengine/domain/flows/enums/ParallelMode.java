/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 并行节点的操作类型
 *
 * @author g00564732
 * @since 2023/08/14
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
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "ParallelMode", code));
    }
}
