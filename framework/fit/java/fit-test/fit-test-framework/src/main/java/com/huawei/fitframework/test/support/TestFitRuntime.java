/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.support;

import com.huawei.fitframework.plugin.RootPlugin;
import com.huawei.fitframework.runtime.support.AbstractFitRuntime;
import com.huawei.fitframework.util.ObjectUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 为测试框架提供运行时环境。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class TestFitRuntime extends AbstractFitRuntime {
    private static final String USER_DIR_KEY = "user.dir";

    private final TestContextConfiguration configuration;

    /**
     * TestFitRuntime的构造函数。
     *
     * @param clazz 测试类 {@link Class}{@code <?>}。
     * @param resolver 测试类的解析器{@link TestClassResolver}。
     */
    public TestFitRuntime(Class<?> clazz, TestClassResolver resolver) {
        super(clazz, null);
        this.configuration = resolver.resolve(clazz);
    }

    @Override
    protected URL locateRuntime() {
        try {
            return new File(System.getProperty(USER_DIR_KEY)).toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to get locate runtime when run test framework.");
        }
    }

    @Override
    protected URLClassLoader obtainSharedClassLoader() {
        return ObjectUtils.cast(TestFitRuntime.class.getClassLoader().getParent());
    }

    @Override
    protected RootPlugin createRootPlugin() {
        return new TestPlugin(this, configuration);
    }
}
