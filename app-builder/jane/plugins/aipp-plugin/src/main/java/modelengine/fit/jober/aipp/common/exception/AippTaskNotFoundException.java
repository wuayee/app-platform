/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.exception;

import lombok.Getter;
import modelengine.fit.jane.common.entity.OperationContext;

/**
 * aipp通用受检异常
 *
 * @author 余坤
 * @since 2024-01-31
 */
@Getter
public class AippTaskNotFoundException extends AippCheckedException {
    private OperationContext context;

    private modelengine.fit.jober.aipp.common.exception.AippErrCode error;

    public AippTaskNotFoundException(OperationContext context,
            modelengine.fit.jober.aipp.common.exception.AippErrCode error) {
        super(context, error);
    }

    public AippTaskNotFoundException(AippErrCode error) {
        super(error);
    }
}
