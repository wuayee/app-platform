/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.aspect.interceptor;

import modelengine.fitframework.aop.annotation.Before;
import modelengine.fitframework.aop.interceptor.AdviceMethodInterceptor;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanFactory;

import java.lang.reflect.Method;

/**
 * 将带有 {@link Before} 注解的方法包装成 {@link AdviceMethodInterceptor}。
 *
 * @author 季聿阶
 * @author 郭龙飞
 * @since 2023-03-08
 */
public class AspectBeforeInterceptorFactory extends AbstractAspectInterceptorFactory {
    public AspectBeforeInterceptorFactory() {
        super(Before.class);
    }

    @Override
    protected AdviceMethodInterceptor createConcreteMethodInterceptor(BeanFactory aspectFactory, Method method) {
        return new AspectBeforeInterceptor(aspectFactory, method);
    }

    @Override
    protected String getExpression(@Nonnull Method method) {
        return this.getAnnotations(method).getAnnotation(Before.class).pointcut();
    }

    @Override
    protected String getArgNames(@Nonnull Method method) {
        return this.getAnnotations(method).getAnnotation(Before.class).argNames();
    }
}
