/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.entity;

import com.huawei.fit.http.server.HttpServerException;

/**
 * 表示消息体相关的异常。
 *
 * @author 季聿阶 j00559309
 * @since 2022-07-14
 */
public class EntityException extends HttpServerException {
    /**
     * 通过异常消息来实例化 {@link EntityException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public EntityException(String message) {
        super(message);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link EntityException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public EntityException(String message, Throwable cause) {
        super(message, cause);
    }
}
