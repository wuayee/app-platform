/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.common.exceptions;

import modelengine.fit.waterflow.common.ErrorCodes;

/**
 * 错误请求异常类。
 *
 * @author 陈镕希
 * @since 1.0
 */
public class BadRequestException extends WaterflowException {
    /**
     * 抛出错误请求异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     */
    public BadRequestException(ErrorCodes error) {
        super(error);
    }

    /**
     * 抛出错误请求异常。
     *
     * @param error 异常枚举的{@link ErrorCodes}。
     * @param args 额外参数。
     */
    public BadRequestException(ErrorCodes error, Object... args) {
        super(error, args);
    }
}
