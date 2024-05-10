/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * form参数异常
 *
 * @author x00649642
 * @since 2024-02-06
 */
public final class FormParamException extends FormException {
    /**
     * 抛出Form参数错误异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     */
    public FormParamException(FormErrCode error) {
        super(error);
    }

    /**
     * 抛出Form参数错误异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param context 请求头信息
     * @param paramName 参数名称。
     */
    public FormParamException(OperationContext context, FormErrCode error, String paramName) {
        super(context, error, paramName);
    }

    /**
     * 抛出Form参数错误异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param context 请求头信息
     */
    public FormParamException(OperationContext context, FormErrCode error) {
        super(context, error);
    }

    /**
     * 抛出Form参数错误异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param paramName 参数名称。
     */
    public FormParamException(FormErrCode error, String paramName) {
        super(error, paramName);
    }
}
