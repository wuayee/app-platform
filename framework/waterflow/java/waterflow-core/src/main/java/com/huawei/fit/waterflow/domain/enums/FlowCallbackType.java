/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.enums;

import static com.huawei.fit.waterflow.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.waterflow.common.exceptions.WaterflowParamException;
import com.huawei.fit.waterflow.domain.parsers.nodes.callbacks.CallbackParser;
import com.huawei.fit.waterflow.domain.parsers.nodes.callbacks.GeneralCallbackParser;
import com.huawei.fit.waterflow.domain.validators.rules.callbacks.CallbackRule;
import com.huawei.fit.waterflow.domain.validators.rules.callbacks.GeneralCallbackRule;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * 流程定义回调函数类型
 *
 * @author l00862071
 * @since 1.0
 */
@Getter
public enum FlowCallbackType {
    GENERAL_CALLBACK("GENERAL_CALLBACK", new GeneralCallbackParser(), new GeneralCallbackRule());

    private final String code;

    private final CallbackParser callbackParser;

    private final CallbackRule callbackRule;

    FlowCallbackType(String code, CallbackParser callbackParser, CallbackRule callbackRule) {
        this.code = code;
        this.callbackParser = callbackParser;
        this.callbackRule = callbackRule;
    }

    /**
     * getCallbackType
     *
     * @param code code
     * @return FlowCallbackType
     */
    public static FlowCallbackType getCallbackType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "FlowCallbackType", code));
    }
}
