/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanDefinitionException;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 为 {@link BeanDestroyer} 提供基于方法的实现。
 *
 * @author 梁济时
 * @since 2022-04-28
 */
public class MethodBeanDestroyer implements BeanDestroyer {
    private final Method method;

    /**
     * 使用用以销毁Bean的方法初始化 {@link MethodBeanDestroyer} 类的新实例。
     *
     * @param method 表示Bean的销毁方法的 {@link Method}。
     * @throws IllegalArgumentException {@code method} 为 {@code null}。
     * @throws com.huawei.fitframework.ioc.BeanDefinitionException {@code method} 被 {@code static} 修饰。
     */
    public MethodBeanDestroyer(Method method) {
        this.method = Validation.notNull(method, "The method to destroy bean cannot be null.");
        if (Modifier.isStatic(this.method.getModifiers())) {
            throw new BeanDefinitionException(StringUtils.format(
                    "Cannot destroy a bean with a static method. [method={0}]",
                    ReflectionUtils.toString(this.method)));
        } else if (method.getParameterCount() > 0) {
            throw new BeanDefinitionException(StringUtils.format(
                    "Cannot destroy a bean with a method that contains parameters. [method={0}]",
                    ReflectionUtils.toString(this.method)));
        } else {
            this.method.setAccessible(true);
        }
    }

    @Override
    public void destroy(Object bean) {
        try {
            method.invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            // ignore
        }
    }
}
