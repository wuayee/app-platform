/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common;

/**
 * 类型不支持异常
 *
 * @author y00679285
 * @since 2024/2/2
 */
public class TypeNotSupportException extends JoberGenericableException {
    public TypeNotSupportException(String message) {
        super(message);
    }

    public TypeNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }
}
