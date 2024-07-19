/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.http.code;

/**
 * 表示通用错误码的枚举。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
public enum CommonRetCodeEnum implements RetCode {
    SUCCESS(0, "Success"),
    INTERNAL_ERROR(500, "Unknown Error");

    private final int code;
    private final String msg;

    CommonRetCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}