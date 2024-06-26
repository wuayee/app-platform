/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * 对应节点配置中的类型
 *
 * @author s00558940
 * @since 2024/4/18
 */
@Getter
public enum MappingNodeType {
    BOOLEAN("BOOLEAN"),
    INTEGER("INTEGER"),
    NUMBER("NUMBER"),
    STRING("STRING"),
    ARRAY("ARRAY"),
    OBJECT("OBJECT"),
    ;

    private String code;

    private static final Set<MappingNodeType> nestedTypes = new HashSet<>(Arrays.asList(ARRAY, OBJECT));

    MappingNodeType(String code) {
        this.code = code;
    }

    public static MappingNodeType get(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "ElementType", code));
    }

    /**
     * 判断是否是复杂类型
     *
     * @param type 目标类型
     * @return 是否是复杂类型
     */
    public static boolean isNestedType(MappingNodeType type) {
        return nestedTypes.contains(type);
    }
}
