/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema.exception;

import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.schema.code.SchemaValidatorRetCode;

/**
 * 表示通过 Schema 校验数据异常。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public class SchemaValidateException extends FitException {
    /**
     * 校验数据异常构造函数。
     *
     * @param code 表示返回码的 {@link SchemaValidatorRetCode}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public SchemaValidateException(SchemaValidatorRetCode code, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args));
    }


    /**
     * 校验数据异常构造函数。
     *
     * @param code 表示返回码的 {@link SchemaValidatorRetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public SchemaValidateException(SchemaValidatorRetCode code, Throwable cause, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args), cause);
    }
}
