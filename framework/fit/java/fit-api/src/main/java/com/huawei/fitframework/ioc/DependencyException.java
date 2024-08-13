/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc;

/**
 * 为与依赖相关的异常提供基类。
 *
 * @author 梁济时
 * @since 2022-11-29
 */
public class DependencyException extends IocException {
    /**
     * 使用异常信息初始化 {@link DependencyException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public DependencyException(String message) {
        super(message);
    }

    /**
     * 使用引发异常的原因初始化 {@link DependencyException} 类的新实例。
     *
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public DependencyException(Throwable cause) {
        super(cause);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link DependencyException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public DependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
