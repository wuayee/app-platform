/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.resolver;

import com.huawei.fitframework.annotation.ScanPackages;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.fitframework.test.domain.util.AnnotationUtils;
import com.huawei.fitframework.util.ReflectionUtils;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 默认的单测类解析器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class DefaultTestClassResolver implements TestClassResolver {
    private static final Set<String> DEFAULT_SCAN_PACKAGES = new HashSet<>(Arrays.asList(
            "com.huawei.fit.integration.mockito",
            "com.huawei.fit.value",
            "com.huawei.fit.serialization",
            "com.huawei.fitframework.validation"));

    @Override
    public TestContextConfiguration resolve(Class<?> clazz) {
        Class<?>[] classes = this.resolveClass(this.getTestConfigurationClass(clazz));
        return TestContextConfiguration.custom()
                .testClass(clazz)
                .classes(classes)
                .scannedPackages(this.scanBeans(classes))
                .mockedBeanFields(this.scanMockBeansFieldSet(clazz))
                .build();
    }

    private Class<?>[] resolveClass(AnnotatedElement element) {
        return AnnotationUtils.getAnnotation(element, FitTestWithJunit.class)
                .map(FitTestWithJunit::classes)
                .orElseGet(() -> new Class<?>[0]);
    }

    /**
     * 递归向父类查找，是否有测试标识，有则返回。
     *
     * @param clazz 表示需要解析的类对象的 {@link Class}{@code <?>}。
     * @return 表示含有测试标识的测试类。
     */
    private Class<?> getTestConfigurationClass(Class<?> clazz) {
        Class<?> superclass = clazz;
        while (superclass != null) {
            if (this.isJunit5TestClass(superclass)) {
                return superclass;
            }
            superclass = superclass.getSuperclass();
        }
        return clazz;
    }

    private boolean isJunit5TestClass(Class<?> clazz) {
        return AnnotationUtils.getAnnotation(clazz, ExtendWith.class).isPresent();
    }

    private Set<String> scanBeans(Class<?>[] classes) {
        Set<String> basePackages = Arrays.stream(classes)
                .flatMap(resolvedClass -> this.getBasePackages(resolvedClass).stream())
                .collect(Collectors.toSet());
        basePackages.addAll(DEFAULT_SCAN_PACKAGES);
        return basePackages;
    }

    private Set<String> getBasePackages(Class<?> clazz) {
        Optional<ScanPackages> opScanPackagesAnnotation = AnnotationUtils.getAnnotation(clazz, ScanPackages.class);
        if (!opScanPackagesAnnotation.isPresent()) {
            return new HashSet<>();
        }
        Set<String> basePackages = new HashSet<>(Arrays.asList(opScanPackagesAnnotation.get().value()));
        if (basePackages.isEmpty()) {
            basePackages.add(clazz.getPackage().getName());
        }
        return basePackages;
    }

    private Set<Field> scanMockBeansFieldSet(Class<?> clazz) {
        return Arrays.stream(ReflectionUtils.getDeclaredFields(clazz))
                .filter(field -> field.isAnnotationPresent(Mocked.class))
                .collect(Collectors.toSet());
    }
}
