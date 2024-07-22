/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.resolver;

import com.huawei.fitframework.inspection.Validation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认的测试上下文的配置类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-20
 */
public class DefaultTestContextConfiguration implements TestContextConfiguration {
    private final Class<?> testClass;
    private final List<Class<?>> classes;
    private final Set<String> scannedPackages;
    private final Set<Field> mockedBeanFields;

    /**
     * 默认测试上下文的配置类构造函数。
     *
     * @param testClass 表示测试类的 {@link Class}。
     * @param classes 表示注入 bean 类型的 {@code Class[]}。
     * @param scannedPackages 表示扫描包路径的 {@link Set}{@code <}{@link String}{@code >}。
     * @param mockedBeanFields 表示 mocked bean 字段的 {@link Set}{@code <}{@link Field}{@code >}。
     */
    public DefaultTestContextConfiguration(Class<?> testClass, Class<?>[] classes, Set<String> scannedPackages,
            Set<Field> mockedBeanFields) {
        List<Class<?>> classList = classes == null ? Collections.emptyList() : Arrays.asList(classes);
        this.testClass = Validation.notNull(testClass, "The test class cannot be null.");
        this.classes = new ArrayList<>(classList);
        this.scannedPackages = Validation.notNull(scannedPackages, "The scanned packages cannot be null.");
        this.mockedBeanFields = Validation.notNull(mockedBeanFields, "The mocked bean fields cannot be null.");
    }

    @Override
    public Class<?> testClass() {
        return this.testClass;
    }

    @Override
    public Class<?>[] classes() {
        return this.classes.toArray(new Class[0]);
    }

    @Override
    public Set<String> scannedPackages() {
        return Collections.unmodifiableSet(this.scannedPackages);
    }

    @Override
    public Set<Field> mockedBeanFields() {
        return Collections.unmodifiableSet(this.mockedBeanFields);
    }

    @Override
    public void merge(TestContextConfiguration configuration) {
        Validation.equals(this.testClass, configuration.testClass(), "The test class must equal");
        this.classes.addAll(Arrays.asList(configuration.classes()));
        this.scannedPackages.addAll(configuration.scannedPackages());
        this.mockedBeanFields.addAll(configuration.mockedBeanFields());
    }

    @Override
    public int hashCode() {
        return this.classes.hashCode();
    }

    /**
     * 为 {@link TestContextConfiguration.Builder} 提供默认实现。
     */
    public static final class Builder implements TestContextConfiguration.Builder {
        private Class<?> testClass;
        private Class<?>[] classes;
        private Set<String> scannedPackages = new HashSet<>();
        private Set<Field> mockedBeanFields = new HashSet<>();

        @Override
        public TestContextConfiguration.Builder testClass(Class<?> testClass) {
            this.testClass = testClass;
            return this;
        }

        @Override
        public TestContextConfiguration.Builder classes(Class<?>[] classes) {
            this.classes = classes;
            return this;
        }

        @Override
        public TestContextConfiguration.Builder scannedPackages(Set<String> scannedPackages) {
            this.scannedPackages = scannedPackages;
            return this;
        }

        @Override
        public TestContextConfiguration.Builder mockedBeanFields(Set<Field> mockedBeanFields) {
            this.mockedBeanFields = mockedBeanFields;
            return this;
        }

        @Override
        public TestContextConfiguration build() {
            return new DefaultTestContextConfiguration(this.testClass,
                    this.classes,
                    this.scannedPackages,
                    this.mockedBeanFields);
        }
    }
}
