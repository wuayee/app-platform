/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fit.http.server.HttpServerResponseException;

/**
 * 表示 Http 请求映射的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-08
 */
public class RequestMappingException extends HttpServerResponseException {
    /**
     * 通过异常消息来实例化 {@link RequestMappingException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public RequestMappingException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link RequestMappingException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public RequestMappingException(String message, Throwable cause) {
        super(HttpResponseStatus.BAD_REQUEST, message, cause);
    }
}
