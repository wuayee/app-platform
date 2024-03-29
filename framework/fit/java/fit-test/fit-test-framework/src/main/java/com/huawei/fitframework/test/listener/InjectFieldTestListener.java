/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.listener;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.test.TestContext;
import com.huawei.fitframework.test.util.AnnotationUtils;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 用于字段注入的监听类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class InjectFieldTestListener implements TestListener {
    @Override
    public void prepareTestInstance(TestContext context) {
        this.injectDependencies(context);
    }

    private void injectDependencies(TestContext context) {
        Object instance = context.testInstance();
        Class<?> clazz = context.testClass();
        Field[] declaredFields = ReflectionUtils.getDeclaredFields(clazz);
        BeanContainer container = context.testPlugin().container();
        Arrays.stream(declaredFields)
                .filter(this::isInjectedFiled)
                .forEach(field -> ReflectionUtils.setField(instance, field, this.getBean(container, field.getType())));
    }

    private boolean isInjectedFiled(Field field) {
        return AnnotationUtils.getAnnotation(field, Fit.class).isPresent();
    }

    private Object getBean(BeanContainer container, Class<?> clazz) {
        return container.factories(clazz)
                .stream()
                .findFirst()
                .map(beanFactory -> clazz.cast(beanFactory.get()))
                .orElseThrow(() -> new IllegalStateException(
                        StringUtils.format("Failed to inject field: Not found available bean. [class={0}].",
                                clazz.getName())));
    }
}