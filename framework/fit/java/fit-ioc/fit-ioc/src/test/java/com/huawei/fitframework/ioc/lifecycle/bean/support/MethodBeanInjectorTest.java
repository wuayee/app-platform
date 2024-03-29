/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean.support;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fitframework.ioc.BeanDefinitionException;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanInjector;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanInjectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@DisplayName("测试 MethodBeanInjector 类")
class MethodBeanInjectorTest {
    @SuppressWarnings("unused")
    static class Bean {
        private static Object staticDependency;

        private Object dependency;

        void dependency(Object dependency) {
            this.dependency = dependency;
        }

        static void staticDependency(Object value) {
            staticDependency = value;
        }
    }

    @Test
    @DisplayName("使用方法注入程序对Bean进行注入")
    void should_inject_value_by_method() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("dependency", Object.class);
        Object value = new byte[0];
        final BeanInjector injector = BeanInjectors.method(method, value);
        Bean bean = new Bean();
        injector.inject(bean);
        assertSame(value, bean.dependency);
    }

    @Test
    @DisplayName("当使用 static 方法注入时抛出异常")
    void should_throw_when_inject_with_static_method() throws NoSuchMethodException {
        Method method = Bean.class.getDeclaredMethod("staticDependency", Object.class);
        Object value = new byte[0];
        assertThrows(BeanDefinitionException.class, () -> new MethodBeanInjector(method, new Object[] {value}));
    }
}
