/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.service;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.runtime.FitRuntime;
import com.huawei.fitframework.test.domain.TestContext;
import com.huawei.fitframework.test.domain.TestFitRuntime;
import com.huawei.fitframework.test.domain.listener.InjectFieldTestListener;
import com.huawei.fitframework.test.domain.mvc.MockMvc;
import com.huawei.fitframework.test.domain.resolver.TestClassResolver;
import com.huawei.fitframework.test.domain.util.TestUtil;

/**
 * 默认的测试框架管理类。
 * <p>当前包括测试上下文和单测类监听类。</p>
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-02
 */
public class DefaultFitTestService implements FitTestService {
    private final TestContext testContext;
    private MockMvc mockMvc;

    public DefaultFitTestService(Class<?> clazz) {
        Validation.notNull(clazz, "The test class to create fit test manager cannot be null.");
        int port = TestUtil.getLocalAvailablePort();
        FitRuntime runtime = new TestFitRuntime(clazz, TestClassResolver.create(), port);
        runtime.start();

        // 每个测试用例都会创建一个 模拟 MVC 实例，只有启用模拟 MVC 测试，才会启动服务端，后续优化 MVC 和端口的申请
        this.mockMvc = new MockMvc(runtime.root(), port);
        runtime.root().container().registry().register(this.mockMvc);

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

    @Override
    public void afterProcess() {
        this.mockMvc.afterProcess();
    }
}