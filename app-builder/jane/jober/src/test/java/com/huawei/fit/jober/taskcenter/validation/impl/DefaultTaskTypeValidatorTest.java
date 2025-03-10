/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link DefaultTaskTypeValidator}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-01 17:08
 */
class DefaultTaskTypeValidatorTest {
    private DefaultTaskTypeValidator defaultTaskTypeValidator;

    @BeforeEach
    void before() {
        defaultTaskTypeValidator = new DefaultTaskTypeValidator(64) {};
    }

    @Nested
    @DisplayName("测试taskId方法")
    class TaskIdTest {
        @Test
        @DisplayName("当输入空taskID时抛出TASK_ID_REQUIRED")
        void emptyTaskIdTaskIdThrowsBadRequestException() {
            String taskId = " ";
            String actualTaskId = StringUtils.trim(taskId);
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> defaultTaskTypeValidator.taskId(actualTaskId));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入非空有效taskID时返回规范化的canonicalizeId")
        void validTaskIdReturnTaskId() {
            String taskId = " FB0C40420DBB45019902F9A5B4C261E9 ";
            String actualTaskId = StringUtils.trim(taskId);
            String result = defaultTaskTypeValidator.taskId(actualTaskId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入非空无效任务ID时抛出TASK_ID_INVALID")
        void invalidTaskIdThrowsBadRequestException() {
            String taskId = " invalidTaskId ";
            String actualTaskId = StringUtils.trim(taskId);
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> defaultTaskTypeValidator.taskId(actualTaskId));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试name方法")
    class NameTest {
        @Test
        @DisplayName("当输入空name时抛出TYPE_NAME_LENGTH_OUT_OF_BOUNDS")
        void emptyNameThrowsBadRequestException() {
            String name = " ";
            String actual = StringUtils.trim(name);
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> defaultTaskTypeValidator.name(actual));
            Assertions.assertEquals(ErrorCodes.TYPE_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入非空name但长度超过最大长度时抛出TYPE_NAME_LENGTH_OUT_OF_BOUNDS")
        void notEmptyNameWithLongLengthThrowsBadRequestException() {
            String name = " fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            String actual = StringUtils.trim(name);
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> defaultTaskTypeValidator.name(actual));
            Assertions.assertEquals(ErrorCodes.TYPE_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入有效name时返回actual")
        void validNameReturnActual() {
            String name = " testName ";
            String actual = StringUtils.trim(name);
            String result = defaultTaskTypeValidator.name(actual);
            Assertions.assertEquals(actual, result);
        }
    }

    @Nested
    @DisplayName("测试parentId方法")
    class ParentIdTest {
        @Test
        @DisplayName("当输入空parentId时返回null")
        void emptyParentIdThrowsBadRequestException() {
            String parentId = " ";
            String actual = StringUtils.trim(parentId);
            String result = defaultTaskTypeValidator.parentId(actual);
            Assertions.assertNull(result);
        }

        @Test
        @DisplayName("当输入有效parentId时返回规范化的canonicalizeId")
        void validParentIdReturnCanonicalizeId() {
            String parentId = " FB0C40420DBB45019902F9A5B4C261E9 ";
            String actual = StringUtils.trim(parentId);
            String result = defaultTaskTypeValidator.parentId(actual);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入非空但无效parentId抛出TYPE_PARENT_ID_INVALID")
        void notEmptyParentIdWithLongLengthThrowsBadRequestException() {
            String parentId = " 123";
            String actual = StringUtils.trim(parentId);
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> defaultTaskTypeValidator.parentId(actual));
            Assertions.assertEquals(ErrorCodes.TYPE_PARENT_ID_INVALID.getMessage(), exception.getMessage());
        }
    }
}





