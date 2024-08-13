/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.async;

import com.huawei.fitframework.aop.interceptor.MethodMatcher;

/**
 * 表示异步执行方法的方法匹配器的工厂。
 *
 * @author 季聿阶
 * @since 2022-11-13
 */
public interface AsyncMethodMatcherFactory {
    /**
     * 根据异步执行线程池名字来创建一个异步执行方法的方法匹配器。
     *
     * @param asyncExecutorName 表示异步执行线程池名字的 {@link String}。
     * @return 表示异步执行方法的方法匹配器的 {@link MethodMatcher}。
     */
    MethodMatcher create(String asyncExecutorName);
}
