/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.exceptions;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.entity.OperationContext;

/**
 * 错误资源地址异常类
 *
 * @author lWX1301876
 * @since 2023-11-15 14:01
 */
public class GoneException extends JobberException {
    /**
     * 抛出错误资源地址异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     */
    public GoneException(ErrorCodes error, OperationContext context) {
        super(error, context);
    }

    /**
     * 抛出错误资源地址异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public GoneException(ErrorCodes error) {
        super(error);
    }

    /**
     * 抛出错误资源地址异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     * @param args 额外参数。
     */
    public GoneException(ErrorCodes error, OperationContext context, Object... args) {
        super(error, context, args);
    }

    /**
     * 抛出错误资源地址异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public GoneException(ErrorCodes error, Object... args) {
        super(error, args);
    }
}
