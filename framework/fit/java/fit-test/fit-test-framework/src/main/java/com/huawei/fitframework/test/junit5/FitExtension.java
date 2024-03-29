/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.junit5;

import com.huawei.fitframework.test.FitTestManager;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * Junit5 的自定义扩展类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-01
 */
public class FitExtension implements BeforeAllCallback, TestInstancePostProcessor {
    private FitTestManager manager;

    @Override
    public void beforeAll(ExtensionContext context) {
        this.manager = FitTestManager.create(context.getRequiredTestClass());
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        this.manager.prepareTestInstance(testInstance);
    }
}
