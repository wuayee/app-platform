/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test;

import com.huawei.fitframework.test.support.DefaultTestContextConfiguration;

/**
 * 测试上下文的配置类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-20
 */
public interface TestContextConfiguration {
    /**
     * 获取单测类的类对象。
     *
     * @return 表示单测类的类对象的 {@link Class}{@code <?>}。
     */
    Class<?> testClass();

    /**
     * 需要往容器上下文注入的类对象。
     *
     * @return 表示需要注入的类对象的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] classes();

    /**
     * {@link TestContextConfiguration} 的构建器。
     */
    interface Builder {
        /**
         * 设置单测类的类对象。
         *
         * @param testClass 需要设置的单测类的类对象的 {@link Class}{@code <?>}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder testClass(Class<?> testClass);

        /**
         * 设置注入的类对象。
         *
         * @param classes 需要设置的注入的类对象的 {@link Class}{@code <?>[]}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder classes(Class<?>[] classes);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link TestContextConfiguration}。
         */
        TestContextConfiguration build();
    }

    /**
     * 获取 {@link TestContextConfiguration} 的构建器。
     *
     * @return 表示 {@link TestContextConfiguration} 的构建器的 {@link Builder}。
     */
    static Builder custom() {
        return new DefaultTestContextConfiguration.Builder();
    }
}
