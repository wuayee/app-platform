/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippParamException;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * App的状态.
 *
 * @author 张越
 * @since 2024-05-24
 */
public enum AppState {
    PUBLISHED("active"),

    INACTIVE("inactive");

    private final String name;

    AppState(String name) {
        this.name = name;
    }

    /**
     * 获取状态名称.
     *
     * @return 状态名称.
     */
    public String getName() {
        return name;
    }

    /**
     * 获取状态
     *
     * @param name 名称
     * @return 发布状态
     */
    public static AppState getAppState(String name) {
        return Arrays.stream(values())
                .filter(value -> StringUtils.equalsIgnoreCase(name, value.getName()))
                .findFirst()
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID, name));
    }
}
