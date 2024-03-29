/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.transaction;

/**
 * 当事务的状态不符合预期时引发的异常。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-24
 */
public class UnexpectedTransactionStateException extends TransactionException {
    /**
     * 使用异常信息初始化 {@link UnexpectedTransactionStateException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public UnexpectedTransactionStateException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link UnexpectedTransactionStateException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public UnexpectedTransactionStateException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link UnexpectedTransactionStateException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public UnexpectedTransactionStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
