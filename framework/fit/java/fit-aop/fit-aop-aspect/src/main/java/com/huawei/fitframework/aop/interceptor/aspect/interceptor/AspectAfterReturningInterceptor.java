/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.interceptor;

import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.annotation.AfterReturning;
import com.huawei.fitframework.aop.interceptor.MethodJoinPoint;
import com.huawei.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import com.huawei.fitframework.aop.interceptor.aspect.interceptor.inject.ParameterInjection;
import com.huawei.fitframework.aop.interceptor.aspect.interceptor.inject.ValueInjection;
import com.huawei.fitframework.aop.interceptor.support.AfterReturningInterceptor;
import com.huawei.fitframework.inspection.Nullable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;

import java.lang.reflect.Method;

/**
 * {@link AfterReturningInterceptor} 的 Aspect 实现。
 *
 * @author 季聿阶 j00559309
 * @author 郭龙飞 gwx900499
 * @since 2022-05-19
 */
public class AspectAfterReturningInterceptor extends AfterReturningInterceptor {
    /**
     * 使用真实拦截的对象和真实拦截的方法来实例化一个 {@link AspectAfterReturningInterceptor}。
     *
     * @param aspectFactory 表示真实拦截的对象的工厂的 {@link BeanFactory}。
     * @param interceptMethod 表示真实拦截的方法的 {@link Method}。
     */
    public AspectAfterReturningInterceptor(BeanFactory aspectFactory, Method interceptMethod) {
        super(aspectFactory, interceptMethod);
        this.validateParameter(interceptMethod);
    }

    private void validateParameter(Method interceptMethod) {
        if (interceptMethod.getParameterCount() > 0) {
            Class<?>[] parameterTypes = interceptMethod.getParameterTypes();
            Validation.isTrue(parameterTypes[0] != ProceedingJoinPoint.class,
                    "The 1st parameter of @AfterReturning interceptor in Aspect cannot be ProceedingJoinPoint.");
        }
    }

    @Override
    protected Object[] getAdvisorArgs(MethodJoinPoint joinPoint, @Nullable Object returnValue,
            @Nullable Throwable throwable) {
        Method method = this.getAdvisorMethod();
        AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(method);
        AfterReturning afterReturning = annotationMetadata.getAnnotation(AfterReturning.class);
        String[] argNames = AspectParameterInjectionHelper.toArgNames(afterReturning.argNames());
        return AspectParameterInjectionHelper.getInjectionArgs(method,
                argNames,
                new ParameterInjection(this.getPointCut(), joinPoint),
                new ValueInjection(afterReturning.returning(), returnValue),
                null);
    }
}
