/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test;

import com.huawei.fitframework.test.plugin.TestPlugin;
import com.huawei.fitframework.test.support.DefaultTestClassResolver;

/**
 * 单测类解析器接口。
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-07
 */
public interface TestClassResolver {
    /**
     * 对单测类进行解析。
     *
     * @param clazz 表示需要解析的类对象的 {@link Class}{@code <?>}。
     * @return 表示解析后的插件类对象。
     */
    TestPlugin resolve(Class<?> clazz);

    /**
     * 创建单测类解析器。
     *
     * @return 表示创建的测试框架的管理类的 {@link FitTestManager}。
     */
    static TestClassResolver create() {
        return new DefaultTestClassResolver();
    }
}
