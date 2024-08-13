/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http;

/**
 * 表示安全问题导致的异常。
 *
 * @author 何天放
 * @since 2024-07-29
 */
public class FitSecurityException extends Exception {
    /**
     * 使用异常信息初始化 {@link FitSecurityException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FitSecurityException(String message) {
        super(message);
    }
}
