/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.exception;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.common.exception.AippNotFoundException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fitframework.plugin.Plugin;

import org.junit.jupiter.api.Test;

/**
 * {@link AippExceptionHandler} 的测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
class AippExceptionHandlerTest {
    Plugin plugin = mock(Plugin.class);

    private final AippExceptionHandler aippExceptionHandler = new AippExceptionHandler(plugin);

    @Test
    void testHandleAippParamException() {
        AippParamException exception = new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL);
        Rsp<?> response = aippExceptionHandler.handleAippParamException(exception);
        assertThat(response.getCode()).isEqualTo(AippErrCode.APP_CHAT_REQUEST_IS_NULL.getCode());
    }

    @Test
    void testHandleAippNotFoundException() {
        AippNotFoundException exception = new AippNotFoundException(AippErrCode.NOT_FOUND, "file");
        Rsp<?> response = aippExceptionHandler.handleAippNotFoundException(exception);
        assertThat(response.getCode()).isEqualTo(AippErrCode.NOT_FOUND.getCode());
    }

    @Test
    void testHandleAippForbiddenException() {
        OperationContext context = new OperationContext();
        AippForbiddenException exception = new AippForbiddenException(context, AippErrCode.FORBIDDEN);
        Rsp<?> response = aippExceptionHandler.handleAippForbiddenException(exception);
        assertThat(response.getCode()).isEqualTo(AippErrCode.FORBIDDEN.getCode());
    }

    @Test
    void testHandleAippException() {
        OperationContext context = new OperationContext();
        AippException exception = new AippException(context, AippErrCode.APP_NOT_FOUND);
        Rsp<?> response = aippExceptionHandler.handleAippException(exception);
        assertThat(response.getCode()).isEqualTo(AippErrCode.APP_NOT_FOUND.getCode());
    }
}