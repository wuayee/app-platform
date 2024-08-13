/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;

import java.util.Arrays;

/**
 * aipp 类型
 *
 * @author 刘信宏
 * @since 2024/02/22
 */
public enum AippTypeEnum {
    /**
     * 普通AIPP。
     */
    NORMAL("NORMAL"),

    /**
     * 预览AIPP
     */
    PREVIEW("PREVIEW");

    private final String type;

    AippTypeEnum(String type) {
        this.type = type;
    }

    public static AippTypeEnum getType(String type) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, type));
    }

    public String type() {
        return this.type;
    }
}
