/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.support;

import com.huawei.fit.security.http.FitSecurityException;

/**
 * 表示文件数量溢出异常。
 *
 * @author 何天放 h00679269
 * @since 2024-07-29
 */
public class FileCountOverflowException extends FitSecurityException {
    /**
     * 使用异常信息初始化 {@link FileCountOverflowException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FileCountOverflowException(String message) {
        super(message);
    }
}
