/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.InstanceValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link InstanceValidatorImpl}对应测试类。
 *
 * @author lWX1301876
 * @since 2023-11-02 14:26
 */
class InstanceValidatorImplTest {
    private InstanceValidator instanceValidator;

    @BeforeEach
    void before() {
        instanceValidator = new InstanceValidatorImpl();
    }

    @Nested
    @DisplayName("测试typeId方法")
    class TypeIdTest {
        @Test
        @DisplayName("当输入空typeId时抛出INSTANCE_SOURCE_REQUIRED")
        void nullTypeIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.typeId(null));
            Assertions.assertEquals(ErrorCodes.INSTANCE_SOURCE_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入有效typeId时返回规范化的canonicalizeId")
        void validTypeIdCanonicalizeId() {
            String typeId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = instanceValidator.typeId(typeId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入无效的typeId时抛出INSTANCE_TYPE_INVALID")
        void invalidTypeIdThrowsBadRequestException() {
            String typeId = "invalidId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.typeId(typeId));
            Assertions.assertEquals(ErrorCodes.INSTANCE_TYPE_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试sourceId方法")
    class SourceIdTest {
        @Test
        @DisplayName("当输入空sourceId时抛出INSTANCE_SOURCE_REQUIRED")
        void nullSourceIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.sourceId(null));
            Assertions.assertEquals(ErrorCodes.INSTANCE_SOURCE_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入有效sourceId时返回规范化的canonicalizeId")
        void validSourceIdCanonicalizeId() {
            String sourceId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = instanceValidator.sourceId(sourceId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入无效的sourceId时抛出INSTANCE_TYPE_INVALID")
        void invalidSourceIdThrowsBadRequestException() {
            String sourceId = "invalidId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.sourceId(sourceId));
            Assertions.assertEquals(ErrorCodes.INSTANCE_SOURCE_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试info方法")
    class InfoTest {
        @Test
        @DisplayName("当传入的info为null时抛出INSTANCE_INFO_REQUIRED")
        void nullInfoThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.info(null));
            Assertions.assertEquals(ErrorCodes.INSTANCE_INFO_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空info则返回info")
        void notNullInfoReturnInfo() {
            Map<String, Object> info = new HashMap<>();
            info.put("name", "John");
            info.put("age", 25);

            Map<String, Object> result = instanceValidator.info(info);
            Assertions.assertEquals(info, result);
        }
    }

    @Nested
    @DisplayName("测试validateTaskId方法")
    class ValidateTaskIdTest {
        @Test
        @DisplayName("当输入空taskId时抛出TASK_ID_REQUIRED")
        void nullTaskIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.validateTaskId(null));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当输入非空且有效taskId时返回规范化的ID")
        void validTaskIdReturnsTaskId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = instanceValidator.validateTaskId(taskId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入非空但无效任务ID时抛出TASK_ID_INVALID")
        void invalidTaskIdReturnsTaskId() {
            String taskId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceValidator.validateTaskId(taskId));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }
}
