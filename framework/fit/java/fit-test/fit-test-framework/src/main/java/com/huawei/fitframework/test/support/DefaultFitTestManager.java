/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.test.FitTestManager;
import com.huawei.fitframework.test.TestClassResolver;
import com.huawei.fitframework.test.TestContext;
import com.huawei.fitframework.test.listener.InjectFieldTestListener;
import com.huawei.fitframework.test.listener.TestListener;
import com.huawei.fitframework.test.plugin.TestPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认的测试框架管理类。
 * <p>当前包括测试上下文和单测类监听类。</p>
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-02
 */
public class DefaultFitTestManager implements FitTestManager {
    private final TestContext testContext;
    private final List<TestListener> listeners = new ArrayList<>();

    public DefaultFitTestManager(Class<?> clazz) {
        Validation.notNull(clazz, "The test class to create fit test manager cannot be null.");
        TestClassResolver resolver = TestClassResolver.create();
        TestPlugin testPlugin = resolver.resolve(clazz);
        this.testContext = new TestContext(clazz, testPlugin);
        this.listeners.add(new InjectFieldTestListener());
    }

    /**
     * 对测试类的实例对象进行处理。
     *
     * @param testInstance 表示测试类实例对象的 {@link Object}。
     */
    @Override
    public void prepareTestInstance(Object testInstance) {
        this.testContext.testInstance(testInstance);
        this.listeners.forEach(listener -> listener.prepareTestInstance(this.testContext));
    }
}