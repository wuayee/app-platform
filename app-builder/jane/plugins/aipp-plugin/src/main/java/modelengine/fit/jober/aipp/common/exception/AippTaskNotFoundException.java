/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.common.exception;

import modelengine.fit.jane.common.entity.OperationContext;

import lombok.Getter;

/**
 * aipp通用受检异常
 *
 * @author 余坤
 * @since 2024-01-31
 */
@Getter
public class AippTaskNotFoundException extends AippCheckedException {
    private OperationContext context;

    private AippErrCode error;

    public AippTaskNotFoundException(OperationContext context, AippErrCode error) {
        super(context, error);
    }

    public AippTaskNotFoundException(AippErrCode error) {
        super(error);
    }
}
