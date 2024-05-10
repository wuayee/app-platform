/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.InstanceEventValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link InstanceEventValidatorImpl}对应测试类。
 *
 * @author lWX1301876
 * @since 2023-11-02 11:21
 */
class InstanceEventValidatorImplTest {
    private InstanceEventValidator instanceEventValidator;

    @BeforeEach
    void before() {
        instanceEventValidator = new InstanceEventValidatorImpl();
    }

    @Nested
    @DisplayName("测试sourceId方法")
    class SourceIdTest {
        @Test
        @DisplayName("当输入空sourceId时返回null")
        void nullSourceIdReturnNull() {
            String result = instanceEventValidator.sourceId(null);
            Assertions.assertNull(result, "null");
        }

        @Test
        @DisplayName("当输入有效sourceId时返回规范化的canonicalizeId")
        void validSourceIdCanonicalizeId() {
            String sourceId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = instanceEventValidator.sourceId(sourceId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入无效的sourceId时抛出SOURCE_SOURCE_INVALID")
        void invalidSourceIdThrowsBadRequestException() {
            String sourceId = "invalidId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceEventValidator.sourceId(sourceId));
            Assertions.assertEquals(ErrorCodes.SOURCE_SOURCE_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试type方法")
    class TypeTest {
        @Test
        @DisplayName("当输入空type时返回null")
        void nullTypeReturnNull() {
            String result = instanceEventValidator.type(null);
            Assertions.assertNull(result, "null");
        }

        @Test
        @DisplayName("当输入有效type时返回事件的枚举值")
        void correctTypeReturn() {
            Assertions.assertEquals("CREATED", instanceEventValidator.type("created"));
            Assertions.assertEquals("MODIFIED", instanceEventValidator.type("modified"));
            Assertions.assertEquals("DELETED", instanceEventValidator.type("deleted"));
        }

        @Test
        @DisplayName("当输入无效的type时抛出INSTANCE_EVENT_TYPE_INCORRECT")
        void incorrectTypeThrowsBadRequestException() {
            String type = "incorrectType";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceEventValidator.type(type));
            Assertions.assertEquals(ErrorCodes.INSTANCE_EVENT_TYPE_INCORRECT.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试fitableId方法")
    class FitableIdTest {
        @Test
        @DisplayName("当输入空fitableId时返回null")
        void nullFitableIdReturnNull() {
            String result = instanceEventValidator.fitableId(null);
            Assertions.assertNull(result);
        }

        @Test
        @DisplayName("当输入有效fitableId时返回规范化的canonicalizeId")
        void validFitableIdCanonicalizeId() {
            String sourceId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = instanceEventValidator.fitableId(sourceId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当输入无效的fitableId时抛出SOURCE_SOURCE_INVALID")
        void invalidFitableIdThrowsBadRequestException() {
            String sourceId = "invalidId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceEventValidator.fitableId(sourceId));
            Assertions.assertEquals(ErrorCodes.FITABLE_ID_INVALID.getMessage(), exception.getMessage());
        }
    }
}
