/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.exceptions;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.entity.OperationContext;

/**
 * 找不到资源异常类。
 *
 * @author 陈镕希
 * @since 2023-08-08
 */
public class NotFoundException extends JobberException {
    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public NotFoundException(ErrorCodes error) {
        super(error);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     * @param args 额外参数。
     */
    public NotFoundException(ErrorCodes error, OperationContext context, Object... args) {
        super(error, context, args);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     */
    public NotFoundException(ErrorCodes error, OperationContext context) {
        super(error, context);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public NotFoundException(ErrorCodes error, Object... args) {
        super(error, args);
    }
}
