/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.exception;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.RetryableException;

/**
 * 表示异步任务提交异常。
 *
 * @author 王成 w00863339
 * @since 2023-11-16
 */
@ErrorCode(AsyncTaskNotAcceptedException.CODE)
public class AsyncTaskNotAcceptedException extends RetryableException {
    /** 表示异步任务提交失败的异常码。 */
    public static final int CODE = 0x7F010007;

    /**
     * 通过异常信息来实例化 {@link AsyncTaskNotAcceptedException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public AsyncTaskNotAcceptedException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link AsyncTaskNotAcceptedException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AsyncTaskNotAcceptedException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link AsyncTaskNotAcceptedException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AsyncTaskNotAcceptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
