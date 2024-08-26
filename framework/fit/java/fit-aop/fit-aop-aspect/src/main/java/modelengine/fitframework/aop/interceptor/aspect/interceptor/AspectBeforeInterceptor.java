/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.aspect.interceptor;

import modelengine.fitframework.aop.ProceedingJoinPoint;
import modelengine.fitframework.aop.annotation.Before;
import modelengine.fitframework.aop.interceptor.MethodJoinPoint;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.inject.AspectParameterInjectionHelper;
import modelengine.fitframework.aop.interceptor.aspect.interceptor.inject.ParameterInjection;
import modelengine.fitframework.aop.interceptor.support.BeforeInterceptor;
import modelengine.fitframework.inspection.Nullable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.ioc.annotation.AnnotationMetadata;

import java.lang.reflect.Method;

/**
 * {@link BeforeInterceptor} 的 Aspect 实现。
 *
 * @author 季聿阶
 * @author 郭龙飞
 * @since 2023-03-08
 */
public class AspectBeforeInterceptor extends BeforeInterceptor {
    /**
     * 使用真实拦截的对象和真实拦截的方法来实例化一个 {@link AspectBeforeInterceptor}。
     *
     * @param aspectFactory 表示真实拦截的对象的工厂 {@link BeanFactory}。
     * @param interceptMethod 表示真实拦截的方法的 {@link Method}。
     */
    public AspectBeforeInterceptor(BeanFactory aspectFactory, Method interceptMethod) {
        super(aspectFactory, interceptMethod);
        this.validateParameter(interceptMethod);
    }

    private void validateParameter(Method interceptMethod) {
        if (interceptMethod.getParameterCount() > 0) {
            Class<?>[] parameterTypes = interceptMethod.getParameterTypes();
            Validation.isTrue(parameterTypes[0] != ProceedingJoinPoint.class,
                    "The 1st parameter of @Before interceptor in Aspect cannot be ProceedingJoinPoint.");
        }
    }

    @Override
    protected Object[] getAdvisorArgs(MethodJoinPoint joinPoint, @Nullable Object returnValue,
            @Nullable Throwable throwable) {
        Method method = this.getAdvisorMethod();
        AnnotationMetadata annotationMetadata = AspectParameterInjectionHelper.getAnnotationMetadata(method);
        Before before = annotationMetadata.getAnnotation(Before.class);
        String[] argNames = AspectParameterInjectionHelper.toArgNames(before.argNames());
        return AspectParameterInjectionHelper.getInjectionArgs(method,
                argNames,
                new ParameterInjection(this.getPointCut(), joinPoint),
                null,
                null);
    }
}
