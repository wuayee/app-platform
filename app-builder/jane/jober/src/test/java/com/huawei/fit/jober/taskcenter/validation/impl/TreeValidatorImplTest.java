/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.validation.TreeValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link TreeValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-03 14:08
 */
class TreeValidatorImplTest {
    private TreeValidator treeValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        treeValidator = new TreeValidatorImpl(64) {};
    }

    @Nested
    @DisplayName("测试name方法")
    class NameTest {
        @Test
        @DisplayName("当传入空name时抛出TREE_NAME_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> treeValidator.name("", context));
            Assertions.assertEquals(ErrorCodes.TREE_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name长度大于最大长度时抛出TREE_NAME_LENGTH_OUT_OF_BOUNDS")
        void longTagThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> treeValidator.name("fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911",
                            context));
            Assertions.assertEquals(ErrorCodes.TREE_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效name时返回name")
        void validTagReturnTag() {
            String result = treeValidator.name("validName", context);
            Assertions.assertEquals("validName", result);
        }
    }

    @Nested
    @DisplayName("测试taskId方法")
    class TaskTest {
        @Test
        @DisplayName("当传入空taskId时返回EMPTY_ID")
        void emptyTaskReturnEMPTYID() {
            String taskId = "";
            String result = treeValidator.taskId(taskId, context);
            Assertions.assertEquals("00000000000000000000000000000000", result);
        }

        @Test
        @DisplayName("当传入非空无效的taskId时抛出TASK_ID_INVALID")
        void invalidTaskIdThrowsBadRequestException() {
            String taskId = "invalidTaskId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> treeValidator.taskId(taskId, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效taskId时返回规范化的canonicalizeId")
        void validTaskIdReturnCanonicalizeId() {
            String result = treeValidator.taskId("FB0C40420DBB45019902F9A5B4C261E9", context);
            Assertions.assertEquals("fb0c40420dbb45019902f9a5b4c261e9", result);
        }
    }
}
