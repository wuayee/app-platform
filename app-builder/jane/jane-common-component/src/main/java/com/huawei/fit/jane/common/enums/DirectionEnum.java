/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * 排序方向(适配天舟规范)
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
@Getter
public enum DirectionEnum {
    DESCEND("desc"),
    ASCEND("asc");

    private final String value;

    DirectionEnum(String value) {
        this.value = value;
    }

    /**
     * getDirection
     *
     * @param value value
     * @return DirectionEnum
     */
    public static DirectionEnum getDirection(String value) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "DirectionEnum", value));
    }
}
