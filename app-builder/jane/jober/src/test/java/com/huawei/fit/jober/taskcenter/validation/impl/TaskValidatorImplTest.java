/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.TaskValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link TaskValidatorImpl}对应测试类。
 *
 * @author lWX1301876
 * @since 2023-11-03 14:08
 */
class TaskValidatorImplTest {
    private TaskValidator taskValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        taskValidator = new TaskValidatorImpl(1, 64) {};
    }

    @Nested
    @DisplayName("测试validateName方法")
    class ValidateNameTest {
        @Test
        @DisplayName("当传入空name时抛出TASK_NAME_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            String name = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> taskValidator.validateName(name, context));
            Assertions.assertEquals(ErrorCodes.TASK_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name长度大于最大长度时抛出TASK_NAME_LENGTH_OUT_OF_BOUNDS")
        void longNameThrowsBadRequestException() {
            String name = "fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> taskValidator.validateName(name, context));
            Assertions.assertEquals(ErrorCodes.TASK_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效name时返回name")
        void validNameReturnName() {
            String name = " validName";
            String result = taskValidator.validateName(name, context);
            Assertions.assertEquals(name, result);
        }
    }

    @Nested
    @DisplayName("测试validateTaskId方法")
    class ValidateTaskIdTest {
        @Test
        @DisplayName("当传入空taskId时抛出TASK_ID_REQUIRED")
        void nullTaskIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> taskValidator.validateTaskId(null, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空且有效taskId时返回规范化的ID")
        void validTaskIdReturnCanonicalizeId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = taskValidator.validateTaskId(taskId, context);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当传入非空但无效任务ID时抛出TASK_ID_INVALID")
        void invalidTaskIdReturnTaskId() {
            String taskId = "invalidTaskId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> taskValidator.validateTaskId(taskId, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }
}
