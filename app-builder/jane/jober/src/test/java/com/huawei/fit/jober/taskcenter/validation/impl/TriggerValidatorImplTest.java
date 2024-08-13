/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.TriggerValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link TriggerValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-06 13:39
 */
class TriggerValidatorImplTest {
    private TriggerValidator triggerValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        triggerValidator = new TriggerValidatorImpl(64) {};
    }

    @Nested
    @DisplayName("测试validateTriggerId方法")
    class ValidateTriggerIdTest {
        @Test
        @DisplayName("当传入的triggerId为null时抛出TRIGGER_REQUIRED")
        void nullTriggerIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validateTriggerId(null, context));
            Assertions.assertEquals(ErrorCodes.TRIGGER_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的非空无效的triggerId时抛出TRIGGER_INVALID")
        void invalidTriggerIdThrowsBadRequestException() {
            String triggerId = "invalidTriggerId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validateTriggerId(triggerId, context));
            Assertions.assertEquals(ErrorCodes.TRIGGER_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的非空有效的TriggerId时返回规范化的canonicalizeId")
        void validTriggerIdReturnCanonicalizeId() {
            String result = triggerValidator.validateTriggerId("FB0C40420DBB45019902F9A5B4C261E9", context);
            Assertions.assertEquals("fb0c40420dbb45019902f9a5b4c261e9", result);
        }
    }

    @Nested
    @DisplayName("测试validateTaskId方法")
    class ValidateTaskIdTest {
        @Test
        @DisplayName("当传入空taskId时抛出TASK_ID_REQUIRED")
        void nullTaskIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validateTaskId(null, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空且有效taskId时返回规范化的ID")
        void validTaskIdReturnCanonicalizeId() {
            String result = triggerValidator.validateTaskId("FB0C40420DBB45019902F9A5B4C261E9", context);
            Assertions.assertEquals("fb0c40420dbb45019902f9a5b4c261e9", result);
        }

        @Test
        @DisplayName("当传入非空但无效任务ID时抛出TASK_ID_INVALID")
        void invalidTaskIdReturnTaskId() {
            String taskId = "invalidTaskId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validateTaskId(taskId, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试validatePropertyName方法")
    class ValidatePropertyNameTest {
        @Test
        @DisplayName("当传入的propertyName为null时抛出PROPERTY_NAME_REQUIRED")
        void emptyPropertyNameThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validatePropertyName(null, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的propertyName长度大于最大长度时抛出PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS")
        void longPropertyNameThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validatePropertyName(
                            "fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911", context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效propertyName时返回propertyName")
        void validPropertyNameReturnPropertyName() {
            String propertyName = "validPropertyName";
            String result = triggerValidator.validatePropertyName(propertyName, context);
            Assertions.assertEquals(propertyName, result);
        }
    }

    @Nested
    @DisplayName("测试validateFitableId方法")
    class ValidateFitableIdTest {
        @Test
        @DisplayName("当传入的fitableId为null时抛出FITABLE_ID_REQUIRED")
        void nullFitableIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validateFitableId(null, context));
            Assertions.assertEquals(ErrorCodes.FITABLE_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空且有效fitableId时返回规范化的ID")
        void validFitableIdReturnCanonicalizeId() {
            String result = triggerValidator.validateFitableId("FB0C40420DBB45019902F9A5B4C261E9", context);
            Assertions.assertEquals("fb0c40420dbb45019902f9a5b4c261e9", result);
        }

        @Test
        @DisplayName("当传入非空但无效任务ID时抛出FITABLE_ID_INVALID")
        void invalidFitableIdReturnTaskId() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> triggerValidator.validateFitableId("invalidFitableId", context));
            Assertions.assertEquals(ErrorCodes.FITABLE_ID_INVALID.getMessage(), exception.getMessage());
        }
    }
}
