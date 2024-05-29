/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.base.common.resp;

import com.huawei.jade.app.engine.base.common.ErrorCode;

import lombok.AllArgsConstructor;

/**
 * app-base 错误码
 *
 * @since 2024-5-27
 *
 */
@AllArgsConstructor
public enum ResponseCode implements ErrorCode {
    /**
     *
     */
    OK(0, "success"),

    /**
     * 参数不合法
     */
    INPUT_PARAM_IS_INVALID(70000000, "Invalid input param"),

    /**
     * 资源不存在
     */
    NOT_FOUND(70000001, "Cannot find entity"),

    /**
     * 未知服务器内部错误
     */
    UNKNOWN(70000002, "Server internal error");

    private final Integer code;

    private final String msg;

    @Override
    public int getErrorCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
