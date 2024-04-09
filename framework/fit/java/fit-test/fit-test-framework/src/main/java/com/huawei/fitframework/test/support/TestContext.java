/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.plugin.RootPlugin;
import com.huawei.fitframework.test.listener.TestListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试上下文类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class TestContext {
    private final Class<?> testClass;
    private final RootPlugin plugin;
    private Object testInstance;

    private final List<TestListener> listeners = new ArrayList<>();

    /**
     * 通过目标测试类和插件来初始化 {@link TestContext}。
     *
     * @param testClass 表示待测试的目标测试类 {@link Class}{@code <?>}。
     * @param plugin 表示待测试的自定义的测试插件实例 {@link RootPlugin}。
     */
    public TestContext(Class<?> testClass, RootPlugin plugin) {
        this.testClass = Validation.notNull(testClass, "The test class to create test context cannot be null.");
        this.plugin = Validation.notNull(plugin, "The root plugin to create test context cannot be null.");
    }

    /**
     * 注册观察者。
     *
     * @param listener 表示待注册的观察者 {@link TestListener}。
     */
    public void registerListener(TestListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    /**
     * 将测试实例中的有 {@link Fit} 注解的字段注入对象。
     *
     * @param testInstance 表示依赖的测试实例 {@link Object}。
     */
    public void prepareTestInstance(Object testInstance) {
        this.testInstance(testInstance);
        this.listeners.forEach(listener -> listener.prepareTestInstance(this));
    }

    /**
     * 获取单测类的类对象。
     *
     * @return 表示单测类的类对象的 {@link Class}{@code <?>}。
     */
    public Class<?> testClass() {
        return this.testClass;
    }

    /**
     * 获取插件对象。
     *
     * @return 表示插件对象的 {@link TestPlugin}。
     */
    public RootPlugin plugin() {
        return this.plugin;
    }

    /**
     * 获取单测类的实例对象。
     *
     * @return 表示单测类实例对象的 {@link Object}。
     */
    public Object testInstance() {
        return this.testInstance;
    }

    /**
     * 设置单测类的实例对象。
     *
     * @param testInstance 表示需要设置单测类的实例对象的 {@link Object}。
     */
    public void testInstance(Object testInstance) {
        this.testInstance = testInstance;
    }
}