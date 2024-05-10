/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-27
 */
public enum FormPropertyTypeEnum {
    Integer("Integer", Integer.class),
    String("String", String.class),
    Boolean("Boolean", Boolean.class),
    Number("Number", Number.class),
    Float("Float", Float.class),
    Byte("Byte", Byte.class),
    Double("Double", Double.class),
    Character("Character", Character.class),
    Object("Object", Object.class),
    Map("Map", java.util.Map.class),
    List("List", java.util.List.class);

    private final String code;
    private final Class<?> clazz;

    FormPropertyTypeEnum(String code, Class<?> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public String code() {
        return this.code;
    }

    public Class<?> clazz() {
        return this.clazz;
    }

    public static Class<?> getClazz(String code) {
        return Arrays.stream(values())
                .filter(item -> StringUtils.startsWithIgnoreCase(code, item.code()))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.FORM_PROPERTY_TYPE_IS_INVALID, code))
                .clazz();
    }
}
