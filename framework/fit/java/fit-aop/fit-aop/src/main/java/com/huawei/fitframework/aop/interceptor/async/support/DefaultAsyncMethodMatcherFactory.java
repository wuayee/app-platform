/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.async.support;

import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.aop.interceptor.async.AsyncMethodMatcher;
import com.huawei.fitframework.aop.interceptor.async.AsyncMethodMatcherFactory;

/**
 * 表示 {@link AsyncMethodMatcherFactory} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-11-13
 */
public class DefaultAsyncMethodMatcherFactory implements AsyncMethodMatcherFactory {
    @Override
    public MethodMatcher create(String asyncExecutorName) {
        return new AsyncMethodMatcher(asyncExecutorName);
    }
}
