/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.definitions.nodes.converter;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * 映射来源类型
 *
 * @author s00558940
 * @since 2024/4/18
 */
@Getter
public enum MappingFromType {
    INPUT("INPUT"),
    REFERENCE("REFERENCE"),
    EXPAND("EXPAND"),
    ;

    private String code;

    private static final Set<MappingFromType> valueTypes = new HashSet<>(Arrays.asList(INPUT, EXPAND));

    MappingFromType(String code) {
        this.code = code;
    }

    public static MappingFromType get(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "MappingFromType", code));
    }

    /**
     * 判断是否是值类型，值类型需要继续根据实际类型解析值的配置
     *
     * @param type 目标类型
     * @return 是否是值类型
     */
    public static boolean isValueType(MappingFromType type) {
        return valueTypes.contains(type);
    }
}
