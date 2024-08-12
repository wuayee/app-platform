/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link AbstractValidator}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-01 12:34
 */
class AbstractValidatorTest {
    private AbstractValidator abstractValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        abstractValidator = new AbstractValidator() {};
    }

    @Nested
    @DisplayName("测试validateTenantId方法")
    class ValidateTenantIdTest {
        @Test
        @DisplayName("当输入的tenantId为null时抛出TENANT_REQUIRED")
        void nullTenantIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validateTenantId(null));
            Assertions.assertEquals(ErrorCodes.TENANT_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入非空tenantId时返回tenantId")
        void validTenantIdReturnTenantId() {
            String tenantId = "validTenantId";
            String result = abstractValidator.validateTenantId(tenantId);
            Assertions.assertEquals(tenantId, result);
        }
    }

    @Nested
    @DisplayName("测试validateTaskId方法")
    class ValidateTaskIdTest {
        @Test
        @DisplayName("当输入空taskId时抛出TASK_ID_REQUIRED")
        void nullTaskIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validateTaskId(null));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入非空且有效taskId时返回规范化的ID")
        void validTaskIdReturnsTaskId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = abstractValidator.validateTaskId(taskId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入非空但无效taskId时抛出TASK_ID_INVALID")
        void invalidTaskIdReturnsTaskId() {
            String taskId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validateTaskId(taskId));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入空taskId以及操作上下文时抛出TASK_ID_REQUIRED并返回context")
        void nullTaskIdAndContextReturnsTaskId() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validateTaskId(null, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入非空有效taskId以及操作上下文时返回规范化的ID以及context")
        void validTaskIdAndContextReturnsTaskId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = abstractValidator.validateTaskId(taskId, context);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入非空无效任务ID以及操作上下文时抛出TASK_ID_INVALID")
        void invalidTaskIdAndContextReturnsTaskId() {
            String taskId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validateTaskId(taskId, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试validatePagination方法")
    class ValidatePaginationTest {
        @Test
        @DisplayName("当输入的偏移量和每页数据条数有效时不抛出异常")
        void validOffsetAndLimit_DoesNotThrowException() {
            long offset = 0L;
            int limit = 100;
            Assertions.assertDoesNotThrow(() -> {
                abstractValidator.validatePagination(offset, limit);
            });
        }

        @Test
        @DisplayName("当输入的偏移量小于0时抛出PAGINATION_OFFSET_INVALID")
        void negativeOffsetThrowsBadRequestException() {
            long offset = -1L;
            int limit = 1000;
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validatePagination(offset, limit));
            Assertions.assertEquals(ErrorCodes.PAGINATION_OFFSET_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入的每页数据条数大于1000时抛出PAGINATION_LIMIT_INVALID")
        void invalidLimitThrowsBadRequestException() {
            long offset = 0L;
            int limit = 1001;
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> abstractValidator.validatePagination(offset, limit));
            Assertions.assertEquals(ErrorCodes.PAGINATION_LIMIT_INVALID.getMessage(), exception.getMessage());
        }
    }
}
