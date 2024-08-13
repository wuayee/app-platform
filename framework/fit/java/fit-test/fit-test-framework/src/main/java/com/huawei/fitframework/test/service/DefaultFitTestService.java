/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.service;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.test.domain.TestContext;
import com.huawei.fitframework.test.domain.TestFitRuntime;
import com.huawei.fitframework.test.domain.listener.DataSourceListener;
import com.huawei.fitframework.test.domain.listener.InjectFieldTestListener;
import com.huawei.fitframework.test.domain.listener.MockMvcListener;
import com.huawei.fitframework.test.domain.listener.MybatisTestListener;
import com.huawei.fitframework.test.domain.listener.ResolverListener;
import com.huawei.fitframework.test.domain.listener.ScanPackageListener;
import com.huawei.fitframework.test.domain.listener.SqlExecuteListener;
import com.huawei.fitframework.test.domain.listener.TestListener;
import com.huawei.fitframework.test.domain.resolver.TestContextConfiguration;
import com.huawei.fitframework.test.domain.util.TestUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 默认的测试框架管理类。
 * <p>当前包括测试上下文和单测类监听类。</p>
 *
 * @author 邬涨财
 * @author 易文渊
 * @since 2023-02-02
 */
public class DefaultFitTestService implements FitTestService {
    private final TestContext testContext;
    private final FitRuntime runtime;

    /**
     * 根据测试类构造 {@link DefaultFitTestService} 的实例。
     * <p>构造流程如下：
     * <ul>
     *     <li>初始化监听端口；</li>
     *     <li>注册所有的监听器；</li>
     *     <li>初始化运行时配置，调用所有监听器的配置接口；</li>
     *     <li>启动测试插件。</li>
     * </ul>
     * </p>
     *
     * @param clazz 表示测试类的 {@link Class}。
     */
    public DefaultFitTestService(Class<?> clazz) {
        Validation.notNull(clazz, "The test class to create fit test manager cannot be null.");
        int port = TestUtils.getLocalAvailablePort();
        List<TestListener> listeners = Arrays.asList(new ResolverListener(),
                new ScanPackageListener(),
                new InjectFieldTestListener.ByFit(),
                new InjectFieldTestListener.BySpy(),
                new MockMvcListener(port),
                new DataSourceListener(),
                new SqlExecuteListener(),
                new MybatisTestListener());
        TestContextConfiguration configuration = TestContextConfiguration.custom().testClass(clazz).build();
        listeners.forEach(testListener -> testListener.config(clazz).ifPresent(configuration::merge));
        this.runtime = new TestFitRuntime(clazz, configuration, port);
        this.runtime.start();
        this.testContext = new TestContext(clazz, this.runtime.root(), listeners);
    }

    @Override
    public void beforeAll() {
        this.testContext.listeners().forEach(testListener -> testListener.beforeTestClass(this.testContext));
    }

    @Override
    public void prepareTestInstance() {
        this.testContext.listeners().forEach(testListener -> testListener.prepareTestInstance(this.testContext));
    }

    @Override
    public void beforeEach() {
        this.testContext.listeners().forEach(testListener -> testListener.beforeTestMethod(this.testContext));
    }

    @Override
    public void afterEach() {
        this.testContext.listeners().forEach(testListener -> testListener.afterTestMethod(this.testContext));
    }

    @Override
    public void afterAll() {
        this.testContext.listeners().forEach(testListener -> testListener.afterTestClass(this.testContext));
        this.runtime.close();
    }

    @Override
    public void setTestInfo(Class<?> clazz, Object testInstance, Method method) {
        this.testContext.testInstance(testInstance);
        this.testContext.testMethod(method);
    }
}