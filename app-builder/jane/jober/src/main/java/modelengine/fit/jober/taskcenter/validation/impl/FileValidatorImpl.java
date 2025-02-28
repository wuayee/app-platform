/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.taskcenter.validation.FileValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

/**
 * {@link FileValidator}的默认实现。
 *
 * @author 陈镕希
 * @since 2023-10-10
 */
@Component
public class FileValidatorImpl implements FileValidator {
    private final int contentLengthMaximum;

    /**
     * DefaultConstructor.
     *
     * @param contentLengthMaximum 默认10485760（10M），文件不得超过此上限。
     */
    public FileValidatorImpl(@Value("${validation.file.content.length.maximum:10485760}") int contentLengthMaximum) {
        this.contentLengthMaximum = contentLengthMaximum;
    }

    @Override
    public void contentLength(int contentLength, OperationContext context) {
        if (contentLength > contentLengthMaximum) {
            throw new BadRequestException(ErrorCodes.FILE_CONTENT_LENGTH_OUT_OF_BOUNDS,
                    ParamUtils.convertOperationContext(context));
        }
    }
}
