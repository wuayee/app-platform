/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.TagValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link TagValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-03 14:08
 */
class TagValidatorImplTest {
    private TagValidator tagValidator;

    @BeforeEach
    void before() {
        tagValidator = new TagValidatorImpl(64, 512, 16) {};
    }

    @Nested
    @DisplayName("测试tag方法")
    class TagTest {
        @Test
        @DisplayName("当传入空tag时抛出TAG_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            String tag = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.tag(tag));
            Assertions.assertEquals(ErrorCodes.TAG_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的tag长度大于最大长度时抛出TAG_LENGTH_OUT_OF_BOUNDS")
        void longTagThrowsBadRequestException() {
            String tag = "fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.tag(tag));
            Assertions.assertEquals(ErrorCodes.TAG_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效tag时返回tag")
        void validTagReturnTag() {
            String tag = " validTag";
            String result = tagValidator.tag(tag);
            Assertions.assertEquals(tag, result);
        }
    }

    @Nested
    @DisplayName("测试description方法")
    class DescriptionTest {
        @Test
        @DisplayName("当传入空description时返回一个空字符串")
        void emptyDescriptionReturnEmptyString() {
            String description = "";
            String result = tagValidator.description(description);
            Assertions.assertEquals("", result);
        }

        @Test
        @DisplayName("当传入的description长度大于最大长度时抛出TAG_DESCRIPTION_LENGTH_OUT_OF_BOUNDS")
        void longDescriptionThrowsBadRequestException() {
            StringBuilder des = new StringBuilder(512);
            for (int i = 0; i < 513; i++) {
                des.append("a");
            }
            String description = des.toString();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.description(description));
            Assertions.assertEquals(ErrorCodes.TAG_DESCRIPTION_LENGTH_OUT_OF_BOUNDS.getMessage(),
                    exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效description时返回description")
        void validDescriptionReturnDescription() {
            String description = " validDescription";
            String result = tagValidator.description(description);
            Assertions.assertEquals(description, result);
        }
    }

    @Nested
    @DisplayName("测试objectType方法")
    class ObjectTypeTest {
        @Test
        @DisplayName("当传入空objectType时抛出TAG_OBJECT_TYPE_REQUIRED")
        void emptyObjectTypeThrowsBadRequestException() {
            String objectType = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.objectType(objectType));
            Assertions.assertEquals(ErrorCodes.TAG_OBJECT_TYPE_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的objectType长度大于最大长度时抛出TAG_OBJECT_TYPE_OUT_OF_BOUNDS")
        void longObjectTypeThrowsBadRequestException() {
            StringBuilder obj = new StringBuilder(16);
            for (int i = 0; i < 17; i++) {
                obj.append("a");
            }
            String objectType = obj.toString();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.objectType(objectType));
            Assertions.assertEquals(ErrorCodes.TAG_OBJECT_TYPE_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效objectType时返回objectType")
        void validObjectTypeReturnObjectType() {
            String objectType = "validObjectType";
            String result = tagValidator.objectType(objectType);
            Assertions.assertEquals(objectType, result);
        }
    }

    @Nested
    @DisplayName("测试objectId方法")
    class ObjectIdTest {
        @Test
        @DisplayName("当传入空objectId时抛出TAG_OBJECT_ID_REQUIRED")
        void emptyObjectIdThrowsBadRequestException() {
            String objectId = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.objectId(objectId));
            Assertions.assertEquals(ErrorCodes.TAG_OBJECT_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空无效的objectId时抛出TAG_OBJECT_ID_FORMAT_INCORRECT")
        void invalidObjectIdThrowsBadRequestException() {
            String objectId = "invalidObjectId";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> tagValidator.objectId(objectId));
            Assertions.assertEquals(ErrorCodes.TAG_OBJECT_ID_FORMAT_INCORRECT.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效objectId时返回规范化的canonicalizeId")
        void validParentIdReturnCanonicalizeId() {
            String parentId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = tagValidator.objectId(parentId);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }
    }
}
