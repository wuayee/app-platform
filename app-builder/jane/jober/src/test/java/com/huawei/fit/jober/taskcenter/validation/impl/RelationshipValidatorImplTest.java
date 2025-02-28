/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.validation.RelationshipValidator;

import modelengine.fit.jober.common.exceptions.NotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link RelationshipValidatorImpl}对应测试类。
 *
 * @author 姚江
 * @since 2023-11-13 14:59
 */
@ExtendWith(MockitoExtension.class)
public class RelationshipValidatorImplTest {
    @Mock
    DynamicSqlExecutor executor;

    private RelationshipValidator relationshipValidator;

    @BeforeEach
    void before() {
        reset(executor);
        relationshipValidator = new RelationshipValidatorImpl(executor);
    }

    @Nested
    @DisplayName("测试validateTaskExistInTenant方法")
    class TestValidateTaskExistInTenant {
        @Test
        @DisplayName("通过测试")
        void testSuccess() {
            when(executor.executeScalar(anyString(), anyList())).thenReturn(1);
            String taskId = "T1";
            String tenantId = "C1";

            Assertions.assertDoesNotThrow(() -> relationshipValidator.validateTaskExistInTenant(taskId, tenantId));
        }

        @Test
        @DisplayName("测试失败")
        void testFailed() {
            String taskId = "T2";
            String tenantId = "C2";
            when(executor.executeScalar(anyString(), anyList())).thenReturn(null);

            NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                    () -> relationshipValidator.validateTaskExistInTenant(taskId, tenantId));

            Assertions.assertEquals(exception.getMessage(), "Task T2 not found in the tenant C2.");
        }
    }

    @Nested
    @DisplayName("测试validateSourceExistInTaskType方法")
    class TestValidateSourceExistInTaskType {
        @Test
        @DisplayName("通过测试")
        void testSuccess() {
            when(executor.executeScalar(anyString(), anyList())).thenReturn(1);
            String sourceId = "S3";
            String typeId = "T3";

            Assertions.assertDoesNotThrow(() -> relationshipValidator.validateSourceExistInTaskType(sourceId, typeId));
        }

        @Test
        @DisplayName("测试失败")
        void testFailed() {
            String sourceId = "S4";
            String typeId = "T4";
            when(executor.executeScalar(anyString(), anyList())).thenReturn(null);

            NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                    () -> relationshipValidator.validateSourceExistInTaskType(sourceId, typeId));

            Assertions.assertEquals(exception.getMessage(), "Source S4 not found in the task type T4.");
        }
    }

    @Nested
    @DisplayName("测试validateTaskTypeExistInTask方法")
    class TestValidateTaskTypeExistInTask {
        @Test
        @DisplayName("通过测试1")
        void testSuccess1() {
            when(executor.executeScalar(anyString(), anyList())).thenReturn(1);
            String typeId = "t5";
            String taskId = "T5";

            Assertions.assertDoesNotThrow(() -> relationshipValidator.validateTaskTypeExistInTask(typeId, taskId));
        }

        @Test
        @DisplayName("通过测试2")
        void testSuccess2() {
            String sql2 = "SELECT 1 FROM task_type AS tt "
                    + "INNER JOIN task_tree_task AS ttt ON ttt.tree_id = tt.tree_id "
                    + "WHERE tt.id = ? AND ttt.task_id = ? " + "LIMIT 1";
            when(executor.executeScalar(eq("SELECT 1 FROM task_type WHERE id = ? AND task_id = ? LIMIT 1"),
                    anyList())).thenReturn(null);
            when(executor.executeScalar(eq(sql2), anyList())).thenReturn(1);
            String typeId = "t6";
            String taskId = "T6";

            Assertions.assertDoesNotThrow(() -> relationshipValidator.validateTaskTypeExistInTask(typeId, taskId));
        }

        @Test
        @DisplayName("测试失败")
        void testFailed() {
            String typeId = "t7";
            String taskId = "T7";
            when(executor.executeScalar(anyString(), anyList())).thenReturn(null);

            NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                    () -> relationshipValidator.validateTaskTypeExistInTask(typeId, taskId));

            Assertions.assertEquals(exception.getMessage(), "Task type t7 not found in the task T7.");
        }
    }
}
