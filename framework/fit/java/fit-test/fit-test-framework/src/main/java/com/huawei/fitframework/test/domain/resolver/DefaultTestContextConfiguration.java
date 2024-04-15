/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.resolver;

import com.huawei.fitframework.inspection.Validation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * 默认的测试上下文的配置类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-20
 */
public class DefaultTestContextConfiguration implements TestContextConfiguration {
    private final Class<?> testClass;
    private final Class<?>[] classes;
    private final Set<String> scannedPackages;
    private final Set<Field> mockedBeanFields;

    public DefaultTestContextConfiguration(Class<?> testClass, Class<?>[] classes, Set<String> scannedPackages,
            Set<Field> mockedBeanFields) {
        this.testClass = Validation.notNull(testClass, "The test class cannot be null.");
        this.classes = Validation.notNull(classes, "The classes cannot be null.");
        this.scannedPackages = Validation.notNull(scannedPackages, "The scanned packages cannot be null.");
        this.mockedBeanFields = Validation.notNull(mockedBeanFields, "The mocked bean fields cannot be null.");
    }

    @Override
    public Class<?> testClass() {
        return this.testClass;
    }

    @Override
    public Class<?>[] classes() {
        return this.classes;
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
    public int hashCode() {
        return Arrays.hashCode(this.classes);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return Arrays.equals(this.classes, ((DefaultTestContextConfiguration) other).classes);
    }

    /**
     * 为 {@link TestContextConfiguration.Builder} 提供默认实现。
     */
    public static final class Builder implements TestContextConfiguration.Builder {
        private Class<?> testClass;
        private Class<?>[] classes;
        private Set<String> scannedPackages;
        private Set<Field> mockedBeanFields;

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
