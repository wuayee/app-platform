/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.validation.NodeValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link NodeValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-02 15:39
 */
class NodeValidatorImplTest {
    private NodeValidator nodeValidator;

    private OperationContext context;

    @BeforeEach
    public void before() {
        nodeValidator = new NodeValidatorImpl(64) {};
    }

    @Nested
    @DisplayName("测试name方法")
    class NameTest {
        @Test
        @DisplayName("当传入name为空时抛出TYPE_NAME_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            String name = "";
            Set<String> names = new HashSet<>();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> nodeValidator.name(name, names, context));
            Assertions.assertEquals(ErrorCodes.TYPE_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name长度大于最大长度时抛出TYPE_NAME_LENGTH_OUT_OF_BOUNDS")
        void longNameThrowsBadRequestException() {
            String name = "fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            Set<String> names = new HashSet<>();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> nodeValidator.name(name, names, context));
            Assertions.assertEquals(ErrorCodes.TYPE_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name已存在于名称集时抛出TYPE_NAME_ALREADY_EXISTS")
        void repeatNameThrowsBadRequestException() {
            String name = "existingName";
            Set<String> names = new HashSet<>();
            names.add(name);
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> nodeValidator.name(name, names, context));
            Assertions.assertEquals(ErrorCodes.TYPE_NAME_ALREADY_EXISTS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name有效则返回name")
        void validNameReturnName() {
            String name = "validName";
            Set<String> names = new HashSet<>();
            String result = nodeValidator.name(name, names, context);
            Assertions.assertEquals(name, result);
        }
    }

    @Nested
    @DisplayName("测试parentId方法")
    class ParentIdTest {
        @Test
        @DisplayName("当传入空parentId时返回EMPTY_ID")
        void emptyParentIdReturnEMPTYID() {
            String parentId = "";
            String result = nodeValidator.parentId(parentId, context);
            Assertions.assertEquals("00000000000000000000000000000000", result);
        }

        @Test
        @DisplayName("当传入非空无效的parentId时抛出NODE_ID_INVALID")
        void invalidParentIdThrowsBadRequestException() {
            String parentId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> nodeValidator.parentId(parentId, context));
            Assertions.assertEquals(ErrorCodes.NODE_ID_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效parentId时返回规范化的canonicalizeId")
        void validParentIdReturnCanonicalizeId() {
            String parentId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = nodeValidator.parentId(parentId, context);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }
    }
}
