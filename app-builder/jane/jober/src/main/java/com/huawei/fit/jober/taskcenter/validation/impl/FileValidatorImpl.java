/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.taskcenter.validation.FileValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;

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
