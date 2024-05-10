/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-24
 */
public enum AppTypeEnum {
    APP("app"),
    TEMPLATE("template"),
    WORKFLOW("workflow");

    private final String code;

    AppTypeEnum(String code) {
        this.code = code;
    }

    public String code() {
        return this.code;
    }
}
