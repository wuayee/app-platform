/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.test.TestContextConfiguration;

import java.util.Arrays;

/**
 * 默认的测试上下文的配置类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-20
 */
public class DefaultTestContextConfiguration implements TestContextConfiguration {
    private final Class<?> testClass;
    private final Class<?>[] classes;

    public DefaultTestContextConfiguration(Class<?> testClass, Class<?>[] classes) {
        Validation.notNull(testClass, "The test class to create test context configuration cannot be null.");
        Validation.notNull(classes, "The classes to create test context configuration cannot be null.");
        this.testClass = testClass;
        this.classes = classes;
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
        public TestContextConfiguration build() {
            return new DefaultTestContextConfiguration(this.testClass, this.classes);
        }
    }
}
