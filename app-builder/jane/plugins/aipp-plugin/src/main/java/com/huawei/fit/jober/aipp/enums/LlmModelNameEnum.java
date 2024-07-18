/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 大模型名称
 *
 * @author l00611472
 * @since 2023/12/26
 */
@Getter
public enum LlmModelNameEnum {
    QWEN_14B("Qwen-14B"),
    QWEN_72B("Qwen-72B"),
    QWEN_VL("qwen-vl"),
    BAICHUAN_13B("BAICHUAN_13B"),
    XIAOHAI("XIAOHAI"),
    UNKNOWN("UNKNOWN");

    private final String value;

    LlmModelNameEnum(String value) {
        this.value = value;
    }

    public static LlmModelNameEnum getLlmModelName(String value) {
        return Arrays.stream(values()).filter(item -> item.name().equalsIgnoreCase(value)).findFirst().orElse(XIAOHAI);
    }
}
