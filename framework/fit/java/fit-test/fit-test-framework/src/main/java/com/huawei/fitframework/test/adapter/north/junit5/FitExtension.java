/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.adapter.north.junit5;

import com.huawei.fitframework.test.service.FitTestService;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * Junit5 的自定义扩展类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-01
 */
public class FitExtension implements BeforeAllCallback, TestInstancePostProcessor, AfterAllCallback {
    private FitTestService service;

    @Override
    public void beforeAll(ExtensionContext context) {
        this.service = FitTestService.create(context.getRequiredTestClass());
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        this.service.prepareTestInstance(testInstance);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        this.service.afterProcess();
    }
}
