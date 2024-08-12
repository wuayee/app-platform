/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.transaction;

/**
 * 表示数据访问的异常。
 *
 * @author 季聿阶
 * @since 2023-05-17
 */
public class DataAccessException extends RuntimeException {
    /**
     * 通过异常消息来实例化 {@link DataAccessException}。
     *
     * @param message 表示异常消息的 {@link String}。
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * 通过异常消息和异常原因来实例化 {@link DataAccessException}。
     *
     * @param message 表示异常消息的 {@link String}。
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 通过异常原因来实例化 {@link DataAccessException}。
     *
     * @param cause 表示异常原因的 {@link Throwable}。
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
