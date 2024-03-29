/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy.support;

import com.huawei.fitframework.aop.interceptor.MethodInterceptor;
import com.huawei.fitframework.aop.proxy.InterceptSupport;

import java.util.Collections;
import java.util.List;

/**
 * {@link InterceptSupport} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-18
 */
public class DefaultInterceptSupport implements InterceptSupport {
    private final Class<?> targetClass;
    private final Object target;
    private final List<MethodInterceptor> methodInterceptors;

    /**
     * 使用被代理对象的类型、被代理对象和适配被代理对象的方法拦截器列表实例化 {@link DefaultInterceptSupport}。
     *
     * @param targetClass 表示被代理对象的类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @param target 表示被代理对象的 {@link Object}。
     * @param methodInterceptors 表示指定方法拦截器列表的 {@link List}{@code <}{@link MethodInterceptor}{@code >}。
     */
    public DefaultInterceptSupport(Class<?> targetClass, Object target, List<MethodInterceptor> methodInterceptors) {
        this.targetClass = targetClass;
        this.target = target;
        this.methodInterceptors = methodInterceptors;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override
    public Object getTarget() {
        return this.target;
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors() {
        return Collections.unmodifiableList(this.methodInterceptors);
    }
}
