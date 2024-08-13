/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import com.huawei.fitframework.aop.interceptor.MethodJoinPoint;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.ioc.BeanFactory;

import java.lang.reflect.Method;

/**
 * 用于在方法调用之前生效的方法拦截器。
 *
 * @author 季聿阶
 * @since 2022-05-11
 */
public class BeforeInterceptor extends AbstractAdviceMethodInterceptor {
    /**
     * 使用拦截建议的对象和拦截建议的方法来实例化一个 {@link BeforeInterceptor}。
     *
     * @param aspectFactory 表示真实拦截的对象的工厂 {@link BeanFactory}。
     * @param advisorMethod 表示真实拦截的方法的 {@link Method}。
     */
    public BeforeInterceptor(BeanFactory aspectFactory, Method advisorMethod) {
        super(aspectFactory, advisorMethod);
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        this.invokeAdvisorPoint(methodJoinPoint, null, null);
        return methodJoinPoint.proceed();
    }
}
