/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * aipp参数异常
 *
 * @author l00611472
 * @since 2024-01-31
 */
public class AippParamException extends AippException {
    /**
     * 抛出Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     */
    public AippParamException(AippErrCode error) {
        super(error);
    }

    /**
     * 抛出Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     * @param paramName 参数名称。
     */
    public AippParamException(OperationContext context, AippErrCode error, String paramName) {
        super(context, error, paramName);
    }

    /**
     * 抛出无入参版本的Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     */
    public AippParamException(OperationContext context, AippErrCode error) {
        super(context, error);
    }

    /**
     * 抛出Aipp参数错误异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param paramName 参数名称。
     */
    public AippParamException(AippErrCode error, String paramName) {
        super(error, paramName);
    }
}
