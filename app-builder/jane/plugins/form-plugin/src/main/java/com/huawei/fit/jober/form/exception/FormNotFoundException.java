/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * form资源不存在异常
 *
 * @author 熊以可
 * @since 2024-02-06
 */
public final class FormNotFoundException extends FormException {
    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param context 请求头信息
     */
    public FormNotFoundException(OperationContext context, FormErrCode error) {
        super(context, error);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     */
    public FormNotFoundException(FormErrCode error) {
        super(error);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param context 请求头信息
     * @param entityName 实体名称。
     */
    public FormNotFoundException(OperationContext context, FormErrCode error, String entityName) {
        super(context, error, entityName);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link FormErrCode}。
     * @param entityName 实体名称。
     */
    public FormNotFoundException(FormErrCode error, String entityName) {
        super(error, entityName);
    }
}
