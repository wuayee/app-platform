/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.runtime;

/**
 * 当 FIT 运行时启动失败时引发的异常。
 *
 * @author 梁济时 l00815032
 * @since 2023-02-27
 */
public class FitRuntimeStartupException extends IllegalStateException {
    /**
     * 使用异常信息初始化 {@link FitRuntimeStartupException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FitRuntimeStartupException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link FitRuntimeStartupException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public FitRuntimeStartupException(String message, Throwable cause) {
        super(message, cause);
    }
}
