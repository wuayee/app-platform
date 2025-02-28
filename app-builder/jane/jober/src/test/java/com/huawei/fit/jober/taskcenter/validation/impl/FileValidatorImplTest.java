/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.validation.FileValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link FileValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-02 10:47
 */
class FileValidatorImplTest {
    private FileValidator fileValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        fileValidator = new FileValidatorImpl(10485760) {};
    }

    @Nested
    @DisplayName("测试contentLength方法")
    class ContentLengthTest {
        @Test
        @DisplayName("当context的length大小超过最大长度大小时抛出FILE_CONTENT_LENGTH_OUT_OF_BOUNDS")
        void contentLengthExceedsBounds() {
            int contentLength = 15000000;
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> fileValidator.contentLength(contentLength, context));
            Assertions.assertEquals(ErrorCodes.FILE_CONTENT_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当context的length大小符合规范时不抛异常")
        void contentLengthWithinBounds() {
            int contentLength = 5000000;
            Assertions.assertDoesNotThrow(() -> fileValidator.contentLength(contentLength, context));
        }
    }
}
