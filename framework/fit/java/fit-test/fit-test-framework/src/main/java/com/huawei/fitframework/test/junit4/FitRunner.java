/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.junit4;

import com.huawei.fitframework.test.FitTestManager;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * Junit4 的自定义扩展类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class FitRunner extends BlockJUnit4ClassRunner {
    FitTestManager manager;

    public FitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.manager = FitTestManager.create(clazz);
    }

    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        this.manager.prepareTestInstance(testInstance);
        return testInstance;
    }
}
