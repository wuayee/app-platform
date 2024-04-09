/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.test.listener.InjectFieldTestListener;

/**
 * 默认的测试框架管理类。
 * <p>当前包括测试上下文和单测类监听类。</p>
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-02
 */
public class DefaultFitTestManager implements FitTestManager {
    private final TestContext testContext;

    public DefaultFitTestManager(Class<?> clazz) {
        Validation.notNull(clazz, "The test class to create fit test manager cannot be null.");
        FitRuntime runtime = new TestFitRuntime(clazz, TestClassResolver.create());
        runtime.start();

        this.testContext = new TestContext(clazz, runtime.root());
        this.testContext.registerListener(new InjectFieldTestListener());
    }

    /**
     * 对测试类的实例对象进行处理。
     *
     * @param testInstance 表示测试类实例对象的 {@link Object}。
     */
    @Override
    public void prepareTestInstance(Object testInstance) {
        this.testContext.prepareTestInstance(testInstance);
    }
}