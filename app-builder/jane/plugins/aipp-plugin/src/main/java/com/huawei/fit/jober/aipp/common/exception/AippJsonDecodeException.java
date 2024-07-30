/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * json解析异常
 *
 * @author x00649642
 * @since 2024-02-23
 */
public class AippJsonDecodeException extends AippException {
    public AippJsonDecodeException(OperationContext context, String message) {
        super(context, AippErrCode.JSON_DECODE_FAILED, message);
    }

    public AippJsonDecodeException(String message) {
        super(AippErrCode.JSON_DECODE_FAILED, message);
    }
}
