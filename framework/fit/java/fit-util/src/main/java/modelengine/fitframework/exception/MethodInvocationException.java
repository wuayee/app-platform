/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package modelengine.fitframework.exception;

/**
 * 当调用方法失败时引发的异常。
 *
 * @author 梁济时
 * @since 1.0
 */
public class MethodInvocationException extends RuntimeException {
    /**
     * 使用异常原因初始化 {@link MethodInvocationException} 类的新实例。
     *
     * @param message 表示异常原因的 {@link String}。
     */
    public MethodInvocationException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link MethodInvocationException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public MethodInvocationException(Throwable cause) {
        super(cause);
    }
}
