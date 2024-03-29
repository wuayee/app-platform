/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.exception;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.FitException;

/**
 * 表示异步任务执行异常。
 *
 * @author 王成 w00863339
 * @since 2023-11-16
 */
@ErrorCode(AsyncTaskExecutionException.CODE)
public class AsyncTaskExecutionException extends FitException {
    /** 表示异步任务执行异常的异常码。 */
    public static final int CODE = 0x7F010010;

    /**
     * 通过异常信息来实例化 {@link AsyncTaskExecutionException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public AsyncTaskExecutionException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link AsyncTaskExecutionException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AsyncTaskExecutionException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link AsyncTaskExecutionException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public AsyncTaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
