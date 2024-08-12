/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.form.dto;

import static com.huawei.fit.jober.common.ErrorCodes.ENUM_CONVERT_FAILED;

import com.huawei.fit.jober.common.exceptions.JobberParamException;

import lombok.Getter;

import java.util.Arrays;

/**
 * elsa Shapes Meta type
 *
 * @author 刘信宏
 * @since 2023-12-25
 */
@Getter
public enum ShapesMetaType {
    STRING("TEXT");

    private final String value;

    ShapesMetaType(String value) {
        this.value = value;
    }

    /**
     * 获取图形元数据类型
     *
     * @param value 值
     * @return 类型
     */
    public static ShapesMetaType getShapesMetaType(String value) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new JobberParamException(ENUM_CONVERT_FAILED, "ShapesMetaType", value));
    }
}
