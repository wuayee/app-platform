/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common.exception;

import com.huawei.fit.jane.common.entity.OperationContext;

/**
 * json编码异常
 *
 * @author x00649642
 * @since 2024-02-23
 */
public class AippJsonEncodeException extends AippException {
    public AippJsonEncodeException(OperationContext context, String message) {
        super(context, AippErrCode.JSON_ENCODE_FAILED, message);
    }

    public AippJsonEncodeException(String message) {
        super(AippErrCode.JSON_ENCODE_FAILED, message);
    }
}
