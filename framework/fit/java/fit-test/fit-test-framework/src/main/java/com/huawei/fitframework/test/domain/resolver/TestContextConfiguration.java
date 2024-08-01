/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.domain.resolver;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 测试上下文的配置类。
 *
 * @author 邬涨财 w00575064
 * @author 易文渊
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
     * 获取需要向容器上下文注入的类对象列表。
     *
     * @return 表示需要向容器上下文注入的类对象列表的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] includeClasses();

    /**
     * 获取不需要向容器上下文注入的类对象列表。
     *
     * @return 表示不需要向容器上下文注入的类对象列表的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] excludeClasses();

    /**
     * 获取测试类依赖的扫描出的包。
     *
     * @return 测试类扫描出的依赖包 {@link Set}{@code <}{@link String}{@code >}。
     */
    Set<String> scannedPackages();

    /**
     * 获取测试类扫描出的模拟的 bean 字段集合。
     *
     * @return 测试类扫描出的模拟的 bean 字段集合 {@link Set}{@code <}{@link Field}{@code >}。
     */
    Set<Field> mockedBeanFields();

    /**
     * 获取需要被侦听的类对象集合。
     *
     * @return 表示需要被侦听的类对象集合的 {@link Set}{@code <}{@link Class}{@code <?>>}。
     */
    Set<Class<?>> toSpyClasses();

    /**
     * 合并另外一个 {@link TestContextConfiguration}。
     *
     * @param configuration 表示另一个上下文配置的 {@link TestContextConfiguration}。
     */
    void merge(TestContextConfiguration configuration);

    /**
     * {@link TestContextConfiguration} 的构建器。
     */
    interface Builder {
        /**
         * 向当前构建器中设置单测类的类对象。
         *
         * @param testClass 表示待设置的单测类的类对象的 {@link Class}{@code <?>}。
         * @return 表示当前构建程序的 {@link Builder}。
         */
        Builder testClass(Class<?> testClass);

        /**
         * 向当前构建器中设置需注入的类对象列表。
         *
         * @param classes 表示待设置的需注入的类对象列表的 {@link Class}{@code <?>[]}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder includeClasses(Class<?>[] classes);

        /**
         * 向当前构建器中设置不需注入的类对象列表。
         *
         * @param classes 表示待设置的不需注入的类对象列表的 {@link Class}{@code <?>[]}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder excludeClasses(Class<?>[] classes);

        /**
         * 向当前构建器中设置待扫描的包。
         *
         * @param basePackages 表示待设置的测试类扫描的包路径的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder scannedPackages(Set<String> basePackages);

        /**
         * 向当前构建器中设置 mocked bean 字段集合。
         *
         * @param mockedBeanFields 设置测试类扫描出的 mocked bean 字段集合 {@link Set}{@code <}{@link Field}{@code >}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder mockedBeanFields(Set<Field> mockedBeanFields);

        /**
         * 向当前构建器中设置需要被侦听的类对象集合。
         *
         * @param toSpyClasses 表示待设置的需要被侦听的类对象集合的 {@link Set}{@code <}{@link Class}{@code <?>>}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        Builder toSpyClasses(Set<Class<?>> toSpyClasses);

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
