/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.exception;

import modelengine.fitframework.exception.ErrorCode;
import modelengine.fitframework.exception.RetryableException;

/**
 * 表示当异步任务未执行完成时引发的异常。
 *
 * @author 王成
 * @since 2023-11-16
 */
@ErrorCode(AsyncTaskNotCompletedException.CODE)
public class AsyncTaskNotCompletedException extends RetryableException {
    /** 表示异步任务未执行完成的异常码。 */
    public static final int CODE = 0x7F010009;

    /**
     * 通过异常信息来实例化 {@link AsyncTaskNotCompletedException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public AsyncTaskNotCompletedException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link AsyncTaskNotCompletedException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AsyncTaskNotCompletedException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link AsyncTaskNotCompletedException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AsyncTaskNotCompletedException(String message, Throwable cause) {
        super(message, cause);
    }
}
