/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy.support;

import com.huawei.fitframework.aop.proxy.InterceptSupport;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Jdk 动态代理的回调。
 *
 * @author 季聿阶
 * @since 2022-05-25
 */
public class JdkDynamicProxy extends AbstractAopProxy implements InvocationHandler {
    /**
     * 使用拦截支持信息实例化 {@link JdkDynamicProxy}。
     *
     * @param support 表示拦截支持信息的 {@link InterceptSupport}。
     */
    public JdkDynamicProxy(InterceptSupport support) {
        super(support);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(proxy, method, args, methodInvocation -> {
            try {
                return ReflectionUtils.invoke(methodInvocation.getTarget(), method, methodInvocation.getArguments());
            } catch (MethodInvocationException e) {
                throw e.getCause();
            }
        });
    }
}
