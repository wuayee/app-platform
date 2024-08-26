/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fit.http.server;

/**
 * 表示 Http 服务端的异常。
 *
 * @author 季聿阶
 * @since 2022-07-08
 */
public class HttpServerException extends RuntimeException {
    /**
     * 通过异常消息来实例化 {@link HttpServerException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public HttpServerException(String message) {
        this(message, null);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link HttpServerException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public HttpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
