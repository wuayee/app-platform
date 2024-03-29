/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.runtime;

import com.huawei.fitframework.plugin.RootPlugin;
import com.huawei.fitframework.runtime.support.AbstractFitRuntime;
import com.huawei.fitframework.test.plugin.TestPlugin;
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

    public TestFitRuntime(Class<?> clazz) {
        super(clazz, null);
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
        return new TestPlugin(this);
    }
}
