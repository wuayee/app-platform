/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.exception;

import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.eval.code.AppEvalRetCode;

/**
 * 表示应用评估异常。
 *
 * @author 易文渊
 * @since 2024-07-20
 */
public class AppEvalException extends FitException {
    /**
     * 应用评估异常构造函数。
     *
     * @param code 表示返回码的 {@link AppEvalRetCode}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public AppEvalException(AppEvalRetCode code, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args));
    }

    /**
     * 应用评估异常构造函数。
     *
     * @param code 表示返回码的 {@link AppEvalRetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public AppEvalException(AppEvalRetCode code, Throwable cause, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args), cause);
    }
}