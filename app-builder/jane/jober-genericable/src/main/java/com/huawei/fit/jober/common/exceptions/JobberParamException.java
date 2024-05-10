/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.exceptions;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.entity.OperationContext;

/**
 * 参数错误抛出异常类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-07-14
 */
public class JobberParamException extends JobberException {
    /**
     * 抛出Jobber参数错误异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public JobberParamException(ErrorCodes error) {
        super(error);
    }

    /**
     * 抛出Jobber参数错误异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     * @param args 额外参数。
     */
    public JobberParamException(ErrorCodes error, OperationContext context, Object... args) {
        super(error, context, args);
    }

    /**
     * 抛出Jobber参数错误异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context 请求头信息
     */
    public JobberParamException(ErrorCodes error, OperationContext context) {
        super(error, context);
    }

    /**
     * 抛出Jobber参数错误异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public JobberParamException(ErrorCodes error, Object... args) {
        super(error, args);
    }
}
