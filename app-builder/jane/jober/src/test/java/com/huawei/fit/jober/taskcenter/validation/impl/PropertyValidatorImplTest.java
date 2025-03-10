/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jane.task.domain.PropertyScope;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.validation.PropertyValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * {@link PropertyValidatorImpl}对应测试类。
 *
 * @author 梁子涵
 * @since 2023-11-02 16:52
 */
class PropertyValidatorImplTest {
    private PropertyValidator propertyValidator;

    private OperationContext context;

    @BeforeEach
    void before() {
        propertyValidator = new PropertyValidatorImpl(1, 64, 512, 16, 16) {};
    }

    @Nested
    @DisplayName("测试validatePropertyId方法")
    class ValidatePropertyIdTest {
        @Test
        @DisplayName("当传入的propertyId为null时抛出PROPERTY_REQUIRED")
        void nullPropertyIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validatePropertyId(null, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的非空无效的propertyId时抛出PROPERTY_INVALID")
        void invalidPropertyIdThrowsBadRequestException() {
            String propertyId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validatePropertyId(propertyId, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_INVALID.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的非空有效的propertyId时返回规范化的canonicalizeId")
        void validPropertyIdThrowsReturnCanonicalizeId() {
            String propertyId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = propertyValidator.validatePropertyId(propertyId, context);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }
    }

    @Nested
    @DisplayName("测试validateTaskId方法")
    class ValidateTaskIdTest {
        @Test
        @DisplayName("当传入空taskId时抛出TASK_ID_REQUIRED")
        void nullTaskIdThrowsBadRequestException() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateTaskId(null, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入非空且有效taskId时返回规范化的ID")
        void validTaskIdReturnsTaskId() {
            String taskId = "FB0C40420DBB45019902F9A5B4C261E9";
            String result = propertyValidator.validateTaskId(taskId, context);
            String canonicalizeId = "fb0c40420dbb45019902f9a5b4c261e9";
            Assertions.assertEquals(canonicalizeId, result);
        }

        @Test
        @DisplayName("当传入非空但无效任务ID时抛出TASK_ID_INVALID")
        void invalidTaskIdReturnsTaskId() {
            String taskId = "123";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateTaskId(taskId, context));
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试validateName方法")
    class ValidateNameTest {
        @Test
        @DisplayName("当传入空name时抛出PROPERTY_NAME_REQUIRED")
        void emptyNameThrowsBadRequestException() {
            String name = "";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateName(name, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_NAME_REQUIRED.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入的name长度大于最大长度时抛出PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS")
        void longNameThrowsBadRequestException() {
            String name = " fb0c40420dbb45019902f9a5b4c261e9fb0c40420dbb45019902f9a5b4c261e911";
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateName(name, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_NAME_LENGTH_OUT_OF_BOUNDS.getMessage(), exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效name时返回name")
        void validNameReturnName() {
            String name = " validName";
            String result = propertyValidator.validateName(name, context);
            Assertions.assertEquals(name, result);
        }
    }

    @Nested
    @DisplayName("测试validateDescription方法")
    class ValidateDescriptionTest {
        @Test
        @DisplayName("当传入空description时返回一个空字符串")
        void emptyDescriptionReturnEmptyString() {
            String description = "";
            String result = propertyValidator.validateDescription(description, context);
            Assertions.assertEquals("", result);
        }

        @Test
        @DisplayName("当传入的description长度超过最大长度时抛出PROPERTY_DESCRIPTION_LENGTH_OUT_OF_BOUNDS")
        void longDescriptionThrowsBadRequestException() {
            StringBuilder des = new StringBuilder(512);
            for (int i = 0; i < 512 + 1; i++) {
                des.append("a");
            }
            String description = des.toString();
            ;
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateDescription(description, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_DESCRIPTION_LENGTH_OUT_OF_BOUNDS.getMessage(),
                    exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效的description时返回description")
        void validDescriptionReturnDescription() {
            String description = "validDescription";
            String result = propertyValidator.validateDescription(description, context);
            Assertions.assertEquals(description, result);
        }
    }

    @Nested
    @DisplayName("测试validateDataType方法")
    class ValidateDataTypeClass {
        @Test
        @DisplayName("当传入空dataType时返回TEXT数据类型")
        void emptyDataType() {
            PropertyDataType defaultDataType = PropertyDataType.TEXT;
            PropertyDataType result = propertyValidator.validateDataType("", context);
            Assertions.assertEquals(defaultDataType, result);
        }

        @Test
        @DisplayName("当传入的dataType长度超过最大长度时抛出PROPERTY_DATATYPE_LENGTH_OUT_OF_BOUNDS")
        void longDataTypeThrowsBadRequestException() {
            StringBuilder data = new StringBuilder(16);
            for (int i = 0; i < 17; i++) {
                data.append("a");
            }
            String dataType = data.toString();
            ;
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateDataType(dataType, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_DATATYPE_LENGTH_OUT_OF_BOUNDS.getMessage(),
                    exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效dataType时将数据类型字符串解析为PropertyDataType枚举类型并返回")
        void validDataTypeReturnPropertyDataType() {
            PropertyDataType defaultDataType = PropertyDataType.TEXT;
            String dataType = "TEXT";
            PropertyDataType result = propertyValidator.validateDataType(dataType, context);
            Assertions.assertEquals(defaultDataType, result);
        }
    }

    @Nested
    @DisplayName("测试validateIdentifiable方法")
    class ValidateIdentifiableTest {
        @Test
        @DisplayName("传入identifiable为null时返回默认的false")
        void nullIdentifiableReturnFalse() {
            Boolean identifiable = null;
            Boolean result = propertyValidator.validateIdentifiable(identifiable);
            Assertions.assertEquals(false, result);
        }

        @Test
        @DisplayName("当传入非空的identifiable时返回identifiable")
        void validIdentifiableReturnIdentifiable() {
            Boolean identifiable = true;
            Boolean result = propertyValidator.validateIdentifiable(identifiable);
            Assertions.assertEquals(true, result);
        }
    }

    @Nested
    @DisplayName("测试validateRequired方法")
    class ValidateRequiredTest {
        @Test
        @DisplayName("当传入的required为null时返回false")
        void nullRequiredReturnFalse() {
            Boolean required = null;
            Boolean result = propertyValidator.validateRequired(required);
            Assertions.assertEquals(false, result);
        }

        @Test
        @DisplayName("当传入非空的required时返回required")
        void validRequiredReturnRequired() {
            Boolean required = true;
            Boolean result = propertyValidator.validateRequired(required);
            Assertions.assertEquals(true, result);
        }
    }

    @Nested
    @DisplayName("测试validateScope方法")
    class ValidateScopeTest {
        @Test
        @DisplayName("当传入空scope时返回PUBLIC")
        void emptyScopeReturnPublic() {
            PropertyScope propertyScope = PropertyScope.PUBLIC;
            String scope = "";
            PropertyScope result = propertyValidator.validateScope(scope, context);
            Assertions.assertEquals(propertyScope, result);
        }

        @Test
        @DisplayName("当传入的scope的长度超过最大长度时抛出PROPERTY_SCOPE_LENGTH_OUT_OF_BOUNDS")
        void longScopeThrowsBadRequestException() {
            StringBuilder sc = new StringBuilder(16);
            for (int i = 0; i < 17; i++) {
                sc.append("a");
            }
            String scope = sc.toString();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> propertyValidator.validateScope(scope, context));
            Assertions.assertEquals(ErrorCodes.PROPERTY_SCOPE_LENGTH_OUT_OF_BOUNDS.getMessage(),
                    exception.getMessage());
        }

        @Test
        @DisplayName("当传入有效scope时将数据类型字符串解析为PropertyScope枚举类型并返回")
        void validScopeReturnPropertyScope() {
            PropertyScope propertyScope = PropertyScope.PRIVATE;
            String scope = "PRIVATE";
            PropertyScope result = propertyValidator.validateScope(scope, context);
            Assertions.assertEquals(propertyScope, result);
        }
    }

    @Nested
    @DisplayName("测试validateAppearance方法")
    class ValidateAppearanceTest {
        @Test
        @DisplayName("当传入空appearance时返回{}")
        void emptyAppearance() {
            String appearance = "";
            String result = propertyValidator.validateAppearance(appearance);
            Assertions.assertEquals("{}", result);
        }

        @Test
        @DisplayName("当传入有效appearance时返回appearance")
        void validAppearanceReturnAppearance() {
            String appearance = "validAppearance";
            String result = propertyValidator.validateAppearance(appearance);
            Assertions.assertEquals(appearance, result);
        }
    }
}
