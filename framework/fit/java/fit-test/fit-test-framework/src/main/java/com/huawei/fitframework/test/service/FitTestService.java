/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.service;

/**
 * 测试框架的管理接口。
 *
 * @author 邬涨财 w00575064
 * @since 2023-02-07
 */
public interface FitTestService {
    /**
     * 对测试类实例进行再处理。
     *
     * @param testInstance 表示测试类实例的 {@link Object}。
     */
    void prepareTestInstance(Object testInstance);

    /**
     * 测试实例执行结束后的处理。
     *
     */
    void afterProcess();

    /**
     * 根据测试类类型创建测试框架的管理类。
     *
     * @param clazz 表示测试类类型的 {@link Class}{@code <?>}。
     * @return 表示创建的测试框架的管理类的 {@link FitTestService}。
     */
    static FitTestService create(Class<?> clazz) {
        return new DefaultFitTestService(clazz);
    }
}