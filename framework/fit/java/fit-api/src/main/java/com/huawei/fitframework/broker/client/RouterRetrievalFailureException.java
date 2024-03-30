/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.broker.client;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.FitException;

/**
 * 表示当获取动态路由器失败时引发的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2023-07-06
 */
@ErrorCode(RouterRetrievalFailureException.CODE)
public class RouterRetrievalFailureException extends FitException {
    /** 表示获取动态路由器失败的异常码。 */
    public static final int CODE = 0x7F010000;

    /**
     * 通过异常信息来实例化 {@link TooManyFitablesException}。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public RouterRetrievalFailureException(String message) {
        super(message);
    }

    /**
     * 通过异常原因来实例化 {@link TooManyFitablesException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RouterRetrievalFailureException(Throwable cause) {
        super(cause);
    }

    /**
     * 通过异常信息和异常原因来实例化 {@link TooManyFitablesException}。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RouterRetrievalFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
