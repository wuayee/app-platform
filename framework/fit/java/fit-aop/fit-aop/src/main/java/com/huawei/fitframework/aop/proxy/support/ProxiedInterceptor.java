/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.aop.interceptor.MethodJoinPoint;
import com.huawei.fitframework.aop.interceptor.support.AbstractMethodInterceptor;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;

/**
 * 调用被代理对象的方法拦截器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-25
 */
public class ProxiedInterceptor extends AbstractMethodInterceptor {
    private final ProxiedInvoker proxiedInvoker;

    /**
     * 使用被代理对象、被代理对象的方法和调用被代理对象的方法来实例化一个 {@link ProxiedInterceptor}。
     *
     * @param proxiedInvoker 表示调用被代理对象的方法的 {@link ProxiedInvoker}。
     */
    public ProxiedInterceptor(ProxiedInvoker proxiedInvoker) {
        this.proxiedInvoker = notNull(proxiedInvoker, "The proxied invoker cannot be null.");
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        return this.proxiedInvoker.invoke(methodJoinPoint.getProxiedInvocation());
    }
}
