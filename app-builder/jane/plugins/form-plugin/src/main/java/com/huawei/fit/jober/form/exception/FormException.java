/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.exception;

import com.huawei.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.exception.FitException;

import lombok.Getter;

import java.text.MessageFormat;

/**
 * 表单业务自定义异常
 *
 * @author 孙怡菲
 * @since 2024/5/10
 */
@Getter
public class FormException extends FitException {
    private OperationContext context;

    private Object[] args;

    /**
     * 抛出Aipp异常。
     *
     * @param context 上下文
     * @param error 异常枚举的{@link FormErrCode}。
     */
    public FormException(OperationContext context, FormErrCode error) {
        super(error.getErrorCode(), error.getMessage());
        this.context = context;
    }

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     */
    public FormException(FormErrCode error) {
        super(error.getErrorCode(), error.getMessage());
    }

    /**
     * 抛出Aipp异常。
     *
     * @param context 上下文
     * @param error 异常枚举的{@link FormErrCode}。
     * @param args 额外参数。
     */
    public FormException(OperationContext context, FormErrCode error, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args));
        this.args = args;
        this.context = context;
    }

    /**
     * 抛出Aipp异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param args 额外参数。
     */
    public FormException(FormErrCode error, Object... args) {
        super(error.getErrorCode(), MessageFormat.format(error.getMessage(), args));
        this.args = args;
    }

    /**
     * 获取字符串格式的错误码。
     *
     * @return error code in {@link String}
     */
    public String getCodeString() {
        return String.valueOf(getCode());
    }
}
