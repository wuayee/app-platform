/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * aipp资源不存在异常
 *
 * @author l00611472
 * @since 2024-01-31
 */
public class AippNotFoundException extends AippException {
    /**
     * 抛出找不到资源异常。
     *
     * @param context 请求头信息
     * @param entityName 资源名称。
     */
    public AippNotFoundException(OperationContext context, String entityName) {
        super(context, AippErrCode.NOT_FOUND, entityName);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param context 请求头信息
     * @param entityName 资源名称。
     */
    public AippNotFoundException(OperationContext context, AippErrCode error, String entityName) {
        super(context, error, entityName);
    }

    /**
     * 抛出找不到资源异常。
     *
     * @param error 异常枚举的{@link AippErrCode}。
     * @param entityName 资源名称。
     */
    public AippNotFoundException(AippErrCode error, String entityName) {
        super(error, entityName);
    }
}
