/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.enums;

/**
 * 应用类型枚举
 *
 * @author 邬涨财
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
