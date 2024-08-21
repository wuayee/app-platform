/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.service.exception;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.FitException;

/**
 * 表示注册中心的异常。
 *
 * @author 李金绪
 * @since 2024-08-22
 */
@ErrorCode(AuthenticationException.CODE)
public class AuthenticationException extends FitException {
    /** 表示认证鉴权通信的异常码。 */
    public static final int CODE = 0x7F000601;

    /**
     * 通过异常信息来实例化 {@link AuthenticationException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link AuthenticationException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link AuthenticationException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
