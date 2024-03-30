/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.FitException;

/**
 * 当无法找到指定的本地调用器时引发的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2024-03-30
 */
@ErrorCode(ClientLocalExecutorNotFoundException.CODE)
public class ClientLocalExecutorNotFoundException extends FitException {
    /** 表示没有服务实现的异常码。 */
    public static final int CODE = 0x7F010002;

    /**
     * 通过异常信息来实例化 {@link ClientLocalExecutorNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ClientLocalExecutorNotFoundException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link ClientLocalExecutorNotFoundException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ClientLocalExecutorNotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link ClientLocalExecutorNotFoundException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public ClientLocalExecutorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
