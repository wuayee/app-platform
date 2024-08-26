/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks.CallbackParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks.GeneralCallbackParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks.CallbackRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks.GeneralCallbackRule;

import java.util.Arrays;
import java.util.Locale;

/**
 * 流程定义回调函数类型
 *
 * @author 李哲峰
 * @since 2023/12/11
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
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "FlowCallbackType", code));
    }
}
