/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;
import com.huawei.fit.jober.aipp.constants.AippConst;

import lombok.Getter;

import java.util.Arrays;

/**
 * 表单端点
 *
 * @author l00611472
 * @since 2023/12/15
 */
@Getter
public enum FormEdgeEnum {
    START(AippConst.ATTR_START_FORM_ID_KEY, AippConst.ATTR_START_FORM_VERSION_KEY),
    END(AippConst.ATTR_END_FORM_ID_KEY, AippConst.ATTR_END_FORM_VERSION_KEY);

    private final String formIdKey;
    private final String versionKey;

    FormEdgeEnum(String formIdKey, String versionKey) {
        this.formIdKey = formIdKey;
        this.versionKey = versionKey;
    }

    public static FormEdgeEnum getFormEdge(String edge) {
        return Arrays.stream(values())
                .filter(item -> item.name().equalsIgnoreCase(edge))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, edge));
    }
}
