/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.code;

import com.huawei.jade.common.model.ModelInfo;

/**
 * 表示通用返回码的枚举。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
public enum CommonRetCode implements RetCode {
    SUCCESS(0, "Success"),
    INTERNAL_ERROR(500, "Unknown error");

    private final int code;
    private final String msg;

    CommonRetCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return this.convertSubSystemCode(ModelInfo.COMMON_ID, this.code);
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}