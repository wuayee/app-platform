/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.support;

import com.huawei.fit.security.http.FitSecurityException;

/**
 * 表示文件大小异常。
 *
 * @author 何天放
 * @since 2024-07-29
 */
public class FileSizeException extends FitSecurityException {
    /**
     * 使用异常信息初始化 {@link FileSizeException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FileSizeException(String message) {
        super(message);
    }
}
