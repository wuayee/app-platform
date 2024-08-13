/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.interceptor;

import com.huawei.fitframework.aop.annotation.AfterThrowing;
import com.huawei.fitframework.aop.interceptor.AdviceMethodInterceptor;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanFactory;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 将带有 {@link AfterThrowing} 注解的方法包装成 {@link AdviceMethodInterceptor}。
 *
 * @author 季聿阶
 * @author 郭龙飞
 * @since 2022-05-14
 */
public class AspectAfterThrowingInterceptorFactory extends AbstractAspectInterceptorFactory {
    public AspectAfterThrowingInterceptorFactory() {
        super(AfterThrowing.class);
    }

    @Override
    protected AdviceMethodInterceptor createConcreteMethodInterceptor(BeanFactory aspectFactory, Method method) {
        return new AspectAfterThrowingInterceptor(aspectFactory, method);
    }

    @Override
    protected String getExpression(@Nonnull Method method) {
        return this.getAnnotations(method).getAnnotation(AfterThrowing.class).pointcut();
    }

    @Override
    protected String getArgNames(@Nonnull Method method) {
        return this.getAnnotations(method).getAnnotation(AfterThrowing.class).argNames();
    }

    @Override
    protected boolean shouldIgnore(@Nonnull Method method, String argName) {
        return Objects.equals(argName, this.getAnnotations(method).getAnnotation(AfterThrowing.class).throwing());
    }
}
