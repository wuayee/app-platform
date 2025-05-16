/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.validation.TenantValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link TenantValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-03 14:08
 */
class TenantValidatorImplTest {
    private TenantValidator tenantValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        tenantValidator = new TenantValidatorImpl(64) {};
    }

    @Nested
    @DisplayName("测试name方法")
    class NameTest {
        @Test
        @DisplayName("当传入空name时抛出TENANT_NAME_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tenantValidator.name("", context));
            Assertions.assertEquals(ErrorCodes.TENANT_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name长度大于最大长度时抛出TENANT_NAME_LENGTH_OUT_OF_BOUNDS")
        void longTagThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tenantValidator.name("fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911",
                            context));
            Assertions.assertEquals(ErrorCodes.TENANT_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效name时返回name")
        void validTagReturnTag() {
            String result = tenantValidator.name("validName", context);
            Assertions.assertEquals("validName", result);
        }
    }
}
