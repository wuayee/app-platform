/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.adapter.north.junit4;

import com.huawei.fitframework.test.service.FitTestService;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Junit4 的自定义扩展类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
public class FitRunner extends BlockJUnit4ClassRunner {
    private FitTestService service;

    public FitRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.service = FitTestService.create(clazz);
    }

    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        this.service.prepareTestInstance(testInstance);
        return testInstance;
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        this.service.afterProcess();
        return super.withAfterClasses(statement);
    }
}
