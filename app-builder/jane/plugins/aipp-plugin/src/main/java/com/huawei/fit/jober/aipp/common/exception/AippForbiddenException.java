/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * aipp禁止操作异常
 *
 * @author l00611472
 * @since 2024-01-31
 */
public class AippForbiddenException extends AippException {
    /**
     * 抛出禁止操作异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     */
    public AippForbiddenException(OperationContext context, AippErrCode error) {
        super(context, error);
    }

    /**
     * 抛出禁止操作异常。
     *
     * @param context 请求头信息
     */
    public AippForbiddenException(OperationContext context) {
        super(context, AippErrCode.FORBIDDEN);
    }

    /**
     * 抛出禁止操作异常。
     */
    public AippForbiddenException() {
        super(AippErrCode.FORBIDDEN);
    }

    /**
     * 抛出禁止操作异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     */
    public AippForbiddenException(AippErrCode error) {
        super(error);
    }
}
