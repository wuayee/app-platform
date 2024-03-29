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
 * 用于在方法调用之后且成功获取返回值后生效的方法拦截器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-11
 */
public class AfterReturningInterceptor extends AbstractAdviceMethodInterceptor {
    /**
     * 使用拦截建议的对象和拦截建议的方法来实例化一个 {@link AfterReturningInterceptor}。
     *
     * @param aspectFactory 表示真实拦截的对象的 {@link BeanFactory}。
     * @param advisorMethod 表示真实拦截的方法的 {@link Method}。
     */
    public AfterReturningInterceptor(BeanFactory aspectFactory, Method advisorMethod) {
        super(aspectFactory, advisorMethod);
    }

    @Nullable
    @Override
    public Object intercept(@Nonnull MethodJoinPoint methodJoinPoint) throws Throwable {
        Object result = methodJoinPoint.proceed();
        this.invokeAdvisorPoint(methodJoinPoint, result, null);
        return result;
    }
}
