/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.exception;

/**
 * 表示可重试的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2022-09-19
 */
@ErrorCode(RetryableException.CODE)
public class RetryableException extends DegradableException {
    /** 表示可重试异常的根异常码。 */
    public static final int CODE = 0x7F000002;

    /**
     * 通过异常信息来实例化 {@link RetryableException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public RetryableException(String message) {
        super(message);
    }

    /**
     * 通过异常码和异常原因来实例化 {@link RetryableException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RetryableException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常码、异常信息和异常原因来实例化 {@link RetryableException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
