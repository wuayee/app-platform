/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 验证任务关联字段
 *
 * @author 罗书强
 * @since 2024-01-04
 */
class TaskRelationValidatorImplTest {
    private TaskRelationValidatorImpl taskRelationValidatorImplUnderTest;

    @BeforeEach
    void setUp() {
        taskRelationValidatorImplUnderTest = new TaskRelationValidatorImpl(32, 16, 32, 16, 16);
    }

    @Test
    @DisplayName("关联关系的唯一标识无效测试")
    void testId() {
        try {
            taskRelationValidatorImplUnderTest.id("");
        } catch (Exception e) {
            assertEquals("Invalid id of task relation.", e.getMessage());
        }
    }

    @Test
    @DisplayName("关联方的唯一标识为空，长度超出限制测试")
    void testObjectId1() {
        try {
            taskRelationValidatorImplUnderTest.objectId1("");
        } catch (Exception e) {
            assertEquals("The objectId1 of task relation is required, but not provided.", e.getMessage());
        }
        try {
            taskRelationValidatorImplUnderTest.objectId1("ad3761558ede44eea470913825752b2a1");
        } catch (Exception e) {
            assertEquals("The objectId1 of task relation is too long.", e.getMessage());
        }
    }

    @Test
    @DisplayName("关联方的类型为空，长度超出限制测试")
    void testObjectType1() {
        try {
            taskRelationValidatorImplUnderTest.objectType1("");
        } catch (Exception e) {
            assertEquals("The objectType1 of task relation is required, but not provided.", e.getMessage());
        }
        try {
            taskRelationValidatorImplUnderTest.objectType1("ad3761558ede44eea470913825752b2a");
        } catch (Exception e) {
            assertEquals("The objectType1 of task relation is too long.", e.getMessage());
        }
    }

    @Test
    @DisplayName("被关联方的唯一标识为空，长度超出限制测试")
    void testObjectId2() {
        try {
            taskRelationValidatorImplUnderTest.objectId2("");
        } catch (Exception e) {
            assertEquals("The objectId2 of task relation is required, but not provided.", e.getMessage());
        }
        try {
            taskRelationValidatorImplUnderTest.objectId2("ad3761558ede44eea470913825752b2a1");
        } catch (Exception e) {
            assertEquals("The objectId2 of task relation is too long.", e.getMessage());
        }
    }

    @Test
    @DisplayName("被关联方的类型为空，长度超出限制测试")
    void testObjectType2() {
        try {
            taskRelationValidatorImplUnderTest.objectType2("");
        } catch (Exception e) {
            assertEquals("The objectType2 of task relation is required, but not provided.", e.getMessage());
        }
        try {
            taskRelationValidatorImplUnderTest.objectType2("ad3761558ede44eea470913825752b2a");
        } catch (Exception e) {
            assertEquals("The objectType2 of task relation is too long.", e.getMessage());
        }
    }

    @Test
    @DisplayName("关联的类型为空，长度超出限制测试")
    void testRelationType() {
        try {
            taskRelationValidatorImplUnderTest.relationType("");
        } catch (Exception e) {
            assertEquals("The relationType of task relation is required, but not provided.", e.getMessage());
        }
        try {
            taskRelationValidatorImplUnderTest.relationType("ad3761558ede44eea470913825752b2a");
        } catch (Exception e) {
            assertEquals("The relationType of task relation is too long.", e.getMessage());
        }
    }
}
