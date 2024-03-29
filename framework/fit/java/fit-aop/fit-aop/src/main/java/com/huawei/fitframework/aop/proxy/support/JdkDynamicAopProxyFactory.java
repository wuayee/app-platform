/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy.support;

import static com.huawei.fitframework.annotation.Order.PRETTY_HIGH;

import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.aop.proxy.AopProxyFactory;
import com.huawei.fitframework.aop.proxy.FitProxy;
import com.huawei.fitframework.aop.proxy.InterceptSupport;
import com.huawei.fitframework.aop.util.ClassLoaderUtils;
import com.huawei.fitframework.util.ClassUtils;

import java.lang.reflect.Proxy;

/**
 * 表示通过 JDK 的动态代理技术实现的 {@link AopProxyFactory}。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-04
 */
@Order(PRETTY_HIGH)
public class JdkDynamicAopProxyFactory implements AopProxyFactory {
    @Override
    public boolean support(Class<?> targetClass) {
        return targetClass.isInterface() || Proxy.isProxyClass(targetClass) || ClassUtils.isLambda(targetClass);
    }

    @Override
    public Object createProxy(InterceptSupport support) {
        ClassLoader classLoader = ClassLoaderUtils.getCommonChildClassLoader(support.getTargetClass(), FitProxy.class)
                .orElseThrow(() -> new IllegalStateException(
                        "Failed to get common child class loader when create proxy by jdk dynamic."));
        return Proxy.newProxyInstance(classLoader, new Class[] {
                support.getTargetClass(), FitProxy.class
        }, new JdkDynamicProxy(support));
    }
}
