/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.type;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

@DisplayName("测试 TypeMatcher 工具类")
@Disabled
class TypeMatcherTestNew {
    @Nested
    @DisplayName("当前为类型")
    class CurrentIsClass {
        @Nested
        @DisplayName("期望为类型")
        class ExpectedIsClass {
            @Test
            @DisplayName("当前类型的可转为期望类型时，返回 true")
            void should_return_true_when_expected_class_is_assignable_from_current() {
                Class<?> current = Integer.class;
                Class<?> expected = Number.class;
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }

            @Test
            @DisplayName("当前类型不可转为期望类型，返回 false")
            void should_return_false_when_expected_class_is_not_assignable_from_current() {
                Class<?> current = Number.class;
                Class<?> expected = Integer.class;
                boolean ret = TypeMatcher.match(current, expected);
                assertFalse(ret);
            }
        }

        @Nested
        @DisplayName("期望为参数化类型")
        class ExpectedIsParameterizedType {
            @Test
            @DisplayName("当前类型可转为目标泛型类型，返回 true")
            void should_return_true_when_expected_class_is_assignable_from_current() {
                Class<?> current = Integer.class;
                ParameterizedType expected = mock(ParameterizedType.class);
                when(expected.getRawType()).thenReturn(Comparable.class);
                when(expected.getActualTypeArguments()).thenReturn(new Type[] {Integer.class});
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }

            @Test
            @DisplayName("当前类型不可转为目标泛型类型，返回 false")
            @Disabled("待实现")
            void should_return_false_when_expected_class_is_not_assignable_from_current() {
                Class<?> current = Integer.class;
                ParameterizedType expected = mock(ParameterizedType.class);
                when(expected.getRawType()).thenReturn(Comparable.class);
                when(expected.getActualTypeArguments()).thenReturn(new Type[] {String.class});
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }
        }

        @Nested
        @DisplayName("期望为")
        class ExpectedIsWildcardType {
            @Test
            @DisplayName("当期望的通配符的上限是类型的基类时，返回 true")
            @Disabled("待实现")
            void should_return_true_when_upper_bounds_is_assignable_from_current() {
                Class<?> current = Integer.class;
                WildcardType expected = mock(WildcardType.class);
                when(expected.getUpperBounds()).thenReturn(new Type[] {Number.class});
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }

            @Test
            @DisplayName("当期望的通配符的上限是类型的子类时，返回 false")
            @Disabled("待实现")
            void should_return_false_when_upper_bounds_is_not_assignable_from_current() {
                Class<?> current = Object.class;
                WildcardType expected = mock(WildcardType.class);
                when(expected.getUpperBounds()).thenReturn(new Type[] {Number.class});
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }

            @Test
            @DisplayName("当期望的通配符的下限是类型的基类时，返回 false")
            @Disabled("待实现")
            void should_return_false_when_lower_bounds_is_assignable_from_current() {
                Class<?> current = Integer.class;
                WildcardType expected = mock(WildcardType.class);
                when(expected.getLowerBounds()).thenReturn(new Type[] {Number.class});
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }

            @Test
            @DisplayName("当期望的通配符下限是类型的子类时，返回 true")
            @Disabled("待实现")
            void should_return_true_when_current_is_assignable_from_lower_bounds() {
                Class<?> current = Number.class;
                WildcardType expected = mock(WildcardType.class);
                when(expected.getLowerBounds()).thenReturn(new Type[] {Integer.class});
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }
        }

        @Nested
        @DisplayName("期望类型是泛型数组")
        class ExpectedIsGenericArray {
            @Test
            @DisplayName("期望类型的组件类型为当前类型的组件类型的基类时，返回 false")
            void should_return_false_when_component_type_is_assignable_from_current() {
                Class<?> current = Integer[].class;
                GenericArrayType expected = mock(GenericArrayType.class);
                ParameterizedType component = mock(ParameterizedType.class);
                when(component.getRawType()).thenReturn(Comparable.class);
                when(component.getActualTypeArguments()).thenReturn(new Type[] {Number.class});
                when(expected.getGenericComponentType()).thenReturn(component);
                boolean ret = TypeMatcher.match(current, expected);
                assertFalse(ret);
            }

            @Test
            @DisplayName("期望类型的组件类型与当前类型的组件类型匹配时，返回 true")
            void should_return_true_when_expected_component_matches_current_component() {
                Class<?> current = Integer[].class;
                ParameterizedType component = mock(ParameterizedType.class);
                WildcardType componentArgument = mock(WildcardType.class);
                when(componentArgument.getUpperBounds()).thenReturn(new Type[] {Number.class});
                when(component.getRawType()).thenReturn(Comparable.class);
                when(component.getActualTypeArguments()).thenReturn(new Type[] {componentArgument});
                GenericArrayType expected = mock(GenericArrayType.class);
                when(expected.getGenericComponentType()).thenReturn(component);
                boolean ret = TypeMatcher.match(current, expected);
                assertTrue(ret);
            }
        }
    }
}
