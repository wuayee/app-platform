/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

import com.huawei.fitframework.exception.ErrorCode;
import com.huawei.fitframework.exception.FitException;

/**
 * JoberGenericable指定异常基类，用于SPI实现时异常抛出与透传。
 *
 * @author 陈镕希 c00572808
 * @since 2023-06-14
 */
@ErrorCode(500)
public class JoberGenericableException extends FitException {
    /**
     * 使用异常信息初始化 {@link JoberGenericableException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public JoberGenericableException(String message) {
        super(message);
    }

    /**
     * 使用异常信息和引发异常的原因初始化 {@link JoberGenericableException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     * @param cause 表示引发异常的原因的 {@link Throwable}。
     */
    public JoberGenericableException(String message, Throwable cause) {
        super(message, cause);
    }
}
