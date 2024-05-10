/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.hllm.model.LlmModel;

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
    QWEN_14B(LlmModel.QWEN_14B.name()),
    QWEN_72B(LlmModel.QWEN_72B.name()),
    QWEN_VL(LlmModel.QWEN_VL.name()),
    BAICHUAN_13B(LlmModel.BAICHUAN_13B.name()),
    XIAOHAI("XIAOHAI"),
    UNKNOWN(LlmModel.UNKNOWN.name());

    private final String value;

    LlmModelNameEnum(String value) {
        this.value = value;
    }

    public static LlmModelNameEnum getLlmModelName(String value) {
        return Arrays.stream(values()).filter(item -> item.name().equalsIgnoreCase(value)).findFirst().orElse(XIAOHAI);
    }
}
