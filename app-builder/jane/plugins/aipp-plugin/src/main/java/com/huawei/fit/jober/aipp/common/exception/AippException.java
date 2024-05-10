/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fitframework.exception.FitException;

import lombok.Getter;

import java.text.MessageFormat;

/**
 * aipp通用异常
 *
 * @author l00611472
 * @since 2024-01-31
 */
@Getter
public class AippException extends FitException {
    private OperationContext context;

    private Object[] args;

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     */
    public AippException(OperationContext context, AippErrCode error) {
        super(error.getErrorCode(), error.getMessage());
        this.context = context;
    }

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     */
    public AippException(AippErrCode error) {
        super(error.getErrorCode(), error.getMessage());
    }

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param args 额外参数。
     */
    public AippException(OperationContext context, AippErrCode error, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args));
        this.args = args;
        this.context = context;
    }

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param args 额外参数。
     */
    public AippException(AippErrCode error, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args));
        this.args = args;
    }
}
