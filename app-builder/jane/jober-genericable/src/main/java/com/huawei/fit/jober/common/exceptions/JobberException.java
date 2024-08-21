/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.exceptions;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.entity.OperationContext;
import modelengine.fitframework.exception.FitException;

import java.text.MessageFormat;

/**
 * 插件 Jobber 的异常基类。
 *
 * @author 陈镕希
 * @since 2023-06-11
 */
public class JobberException extends FitException {
    private OperationContext context;

    private Object[] args;

    /**
     * 抛出Jobber异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context context
     */
    public JobberException(ErrorCodes error, OperationContext context) {
        super(error.getErrorCode(), error.getMessage());
        this.context = context;
    }

    /**
     * 抛出Jobber异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public JobberException(ErrorCodes error) {
        super(error.getErrorCode(), error.getMessage());
    }

    /**
     * 抛出Jobber异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param context context
     * @param args 额外参数。
     */
    public JobberException(ErrorCodes error, OperationContext context, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args));
        this.args = args;
        this.context = context;
    }

    /**
     * 抛出Jobber异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public JobberException(ErrorCodes error, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args));
        this.args = args;
    }

    /**
     * 抛出Jobber异常。
     *
     * @param cause 表示异常原因的 Throwable。
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public JobberException(Throwable cause, ErrorCodes error, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args), cause);
        this.args = args;
    }

    public OperationContext getContext() {
        return context;
    }

    public Object[] getArgs() {
        return args;
    }
}
