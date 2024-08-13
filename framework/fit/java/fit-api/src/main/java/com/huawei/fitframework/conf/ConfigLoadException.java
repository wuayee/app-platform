/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.conf;

/**
 * 当加载配置失败时引发的异常。
 *
 * @author 梁济时
 * @since 2022-05-25
 */
public class ConfigLoadException extends RuntimeException {
    /**
     * 使用异常信息和引发异常的原因初始化 {@link ConfigLoadException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ConfigLoadException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link ConfigLoadException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
