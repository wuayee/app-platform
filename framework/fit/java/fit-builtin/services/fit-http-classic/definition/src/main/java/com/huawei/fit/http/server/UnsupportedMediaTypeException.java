/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server;

import com.huawei.fit.http.protocol.HttpResponseStatus;

/**
 * 表示无法支持的 {@code Content-Type} 的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2023-05-17
 */
public class UnsupportedMediaTypeException extends HttpServerResponseException {
    /**
     * 通过异常消息来实例化 {@link UnsupportedMediaTypeException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public UnsupportedMediaTypeException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link UnsupportedMediaTypeException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public UnsupportedMediaTypeException(String message, Throwable cause) {
        super(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE, message, cause);
    }
}
