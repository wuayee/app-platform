/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.security.http.support;

import com.huawei.fit.security.http.FitSecurityException;

/**
 * 表示压缩文件异常。
 *
 * @author 何天放
 * @since 2024-07-29
 */
public class ZippedFileException extends FitSecurityException {
    /**
     * 使用异常信息初始化 {@link ZippedFileException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public ZippedFileException(String message) {
        super(message);
    }
}
