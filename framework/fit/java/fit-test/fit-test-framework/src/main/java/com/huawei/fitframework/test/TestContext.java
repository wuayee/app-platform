/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.test.plugin.TestPlugin;

/**
 * 测试上下文类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class TestContext {
    private final Class<?> testClass;
    private final TestPlugin testPlugin;
    private Object testInstance;

    public TestContext(Class<?> testClass, TestPlugin testPlugin) {
        Validation.notNull(testClass, "The test class to create test context cannot be null.");
        Validation.notNull(testPlugin, "The test plugin to create test context cannot be null.");
        this.testClass = testClass;
        this.testPlugin = testPlugin;
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
    public TestPlugin testPlugin() {
        return this.testPlugin;
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