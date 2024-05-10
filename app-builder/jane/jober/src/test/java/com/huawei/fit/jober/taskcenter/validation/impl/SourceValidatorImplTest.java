/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.validation.SourceValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link SourceValidatorImpl}对应测试类。
 *
 * @author lWX1301876
 * @since 2023-11-03 14:08
 */
class SourceValidatorImplTest {
    private SourceValidator sourceValidator;

    @BeforeEach
    void before() {
        sourceValidator = new SourceValidatorImpl(64, 32, 64) {};
    }

    @Nested
    @DisplayName("测试validateSourceId方法")
    class ValidateSourceIdTest {
        @Test
        @DisplayName("当传入的sourceId为null时抛出SOURCE_SOURCE_REQUIRED")
        void nullSourceIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceId(null));
            Assertions.assertEquals(ErrorCodes.SOURCE_SOURCE_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的非空无效的sourceId时抛出SOURCE_SOURCE_INVALID")
        void invalidSourceIdThrowsBadRequestException() {
            String propertyId = "invalidSourceId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceId(propertyId));
            Assertions.assertEquals(ErrorCodes.SOURCE_SOURCE_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的非空有效的sourceId时返回规范化的canonicalizeId")
        void validPropertyIdThrowsReturnCanonicalizeId() {
            String propertyId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = sourceValidator.validateSourceId(propertyId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }
    }

    @Nested
    @DisplayName("测试validateSourceName方法")
    class ValidateSourceNameTest {
        @Test
        @DisplayName("当传入空name时抛出SOURCE_NAME_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            String name = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceName(name));
            Assertions.assertEquals(ErrorCodes.SOURCE_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name长度大于最大长度时抛出SOURCE_NAME_LENGTH_OUT_OF_BOUNDS")
        void longNameThrowsBadRequestException() {
            String name = " fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceName(name));
            Assertions.assertEquals(ErrorCodes.SOURCE_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效name时返回name")
        void validNameReturnName() {
            String name = " validName";
            String result = sourceValidator.validateSourceName(name);
            Assertions.assertEquals(name, result);
        }
    }

    @Nested
    @DisplayName("测试validateSourceApp方法")
    class ValidateSourceAppTest {
        @Test
        @DisplayName("当传入空app时抛出SOURCE_APP_REQUIRED")
        void emptyAppThrowsBadRequestException() {
            String app = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceApp(app));
            Assertions.assertEquals(ErrorCodes.SOURCE_APP_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的app长度大于最大长度时抛出SOURCE_APP_LENGTH_OUT_OF_BOUNDS")
        void longAppThrowsBadRequestException() {
            String app = " fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceApp(app));
            Assertions.assertEquals(ErrorCodes.SOURCE_APP_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效app时返回app")
        void validAppReturnApp() {
            String app = " validApp";
            String result = sourceValidator.validateSourceApp(app);
            Assertions.assertEquals(app, result);
        }
    }

    @Nested
    @DisplayName("测试validateSourceType方法")
    class ValidateSourceTypeTest {
        @Test
        @DisplayName("当传入空type时抛出SOURCE_TYPE_REQUIRED")
        void emptyTypeThrowsBadRequestException() {
            String type = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceType(type));
            Assertions.assertEquals(ErrorCodes.SOURCE_TYPE_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的type长度大于最大长度时抛出SOURCE_TYPE_LENGTH_OUT_OF_BOUNDS")
        void longTypeThrowsBadRequestException() {
            String type = " fb0c40420dbb45019902f9a5b4c261e1";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateSourceType(type));
            Assertions.assertEquals(ErrorCodes.SOURCE_TYPE_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效type时将数据类型字符串解析为SourceType枚举类型并返回")
        void validTypeReturnSourceType() {
            SourceType sourceType = SourceType.SCHEDULE;
            String type = "SCHEDULE";
            SourceType result = sourceValidator.validateSourceType(type);
            Assertions.assertEquals(sourceType, result);
        }
    }

    @Nested
    @DisplayName("测试validateTaskId方法")
    class ValidateTaskIdTest {
        @Test
        @DisplayName("当传入空taskId时抛出TASK_ID_REQUIRED")
        void nullTaskIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateTaskId(null));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空且有效taskId时返回规范化的ID")
        void validTaskIdReturnCanonicalizeId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = sourceValidator.validateTaskId(taskId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当传入非空但无效任务ID时抛出TASK_ID_INVALID")
        void invalidTaskIdReturnTaskId() {
            String taskId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateTaskId(taskId));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试validateTypeId方法")
    class ValidateTypeIdTest {
        @Test
        @DisplayName("当传入的typeId为null时返回TYPE_ID_REQUIRED")
        void nullTypeIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateTypeId(null));
            Assertions.assertEquals(ErrorCodes.TYPE_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空且有效typeId时返回规范化的ID")
        void validTaskIdReturnTaskId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = sourceValidator.validateTypeId(taskId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当传入非空但无效任务ID时抛出TYPE_ID_INVALID")
        void invalidTypeIdReturnTypeId() {
            String typeId = "invalidTypeId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> sourceValidator.validateTypeId(typeId));
            Assertions.assertEquals(ErrorCodes.TYPE_ID_INVALID.getMessage(), exception.getMessage());
        }
    }
}
