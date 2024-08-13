/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.support;

import com.huawei.fit.security.http.FitSecurityException;

/**
 * 表示文件类型异常。
 *
 * @author 何天放
 * @since 2024-07-29
 */
public class FileTypeException extends FitSecurityException {
    /**
     * 使用异常信息初始化 {@link FileTypeException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public FileTypeException(String message) {
        super(message);
    }
}
