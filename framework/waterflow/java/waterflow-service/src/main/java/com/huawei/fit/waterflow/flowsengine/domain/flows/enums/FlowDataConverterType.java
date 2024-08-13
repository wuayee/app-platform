/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.enums;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter.FlowDataConverterParser;
import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.nodes.converter.MappingFlowDataConverterParser;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

/**
 * 流程数据转换器的类型
 *
 * @author 宋永坦
 * @since 2024/4/17
 */
@Getter
public enum FlowDataConverterType {
    MAPPING_CONVERTER("MAPPING_CONVERTER", new MappingFlowDataConverterParser());

    private final String code;

    private final FlowDataConverterParser parser;

    FlowDataConverterType(String code, FlowDataConverterParser parser) {
        this.code = code;
        this.parser = parser;
    }

    /**
     * getJoberType
     *
     * @param code code
     * @return FlowJoberType
     */
    public static FlowDataConverterType getType(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "FlowDataConverterType", code));
    }
}
