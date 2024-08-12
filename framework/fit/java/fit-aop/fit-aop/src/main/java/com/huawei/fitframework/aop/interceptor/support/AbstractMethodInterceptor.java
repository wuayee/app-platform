/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import com.huawei.fitframework.aop.interceptor.MethodInterceptor;
import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.aop.interceptor.MethodPointcut;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 方法拦截器的通用抽象实现。
 *
 * @author 季聿阶
 * @since 2022-05-28
 */
public abstract class AbstractMethodInterceptor implements MethodInterceptor {
    private final MethodPointcut methodPointcut;

    /**
     * 直接实例化 {@link AbstractMethodInterceptor}。
     */
    protected AbstractMethodInterceptor() {
        this(null);
    }

    /**
     * 使用一系列方法拦截器来实例化 {@link AbstractMethodInterceptor}。
     *
     * @param methodMatchers 表示一系列方法拦截器的 {@link List}{@code <}{@link MethodMatcher}{@code >}。
     */
    protected AbstractMethodInterceptor(List<MethodMatcher> methodMatchers) {
        this.methodPointcut = new DefaultMethodPointcut();
        if (CollectionUtils.isEmpty(methodMatchers)) {
            return;
        }
        methodMatchers.stream()
                .filter(Objects::nonNull)
                .forEach(methodMatcher -> this.methodPointcut.matchers().add(methodMatcher));
    }

    @Nonnull
    @Override
    public MethodPointcut getPointCut() {
        return this.methodPointcut;
    }
}
