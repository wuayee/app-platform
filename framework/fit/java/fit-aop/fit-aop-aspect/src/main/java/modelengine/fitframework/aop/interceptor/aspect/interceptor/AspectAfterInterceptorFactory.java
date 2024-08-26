/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.aspect.interceptor;

import modelengine.fitframework.aop.annotation.After;
import modelengine.fitframework.aop.interceptor.AdviceMethodInterceptor;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.ioc.BeanFactory;

import java.lang.reflect.Method;

/**
 * 将带有 {@link After} 注解的方法包装成 {@link AdviceMethodInterceptor}。
 *
 * @author 季聿阶
 * @author 郭龙飞
 * @since 2022-05-14
 */
public class AspectAfterInterceptorFactory extends AbstractAspectInterceptorFactory {
    public AspectAfterInterceptorFactory() {
        super(After.class);
    }

    @Override
    protected AdviceMethodInterceptor createConcreteMethodInterceptor(BeanFactory aspectFactory, Method method) {
        return new AspectAfterInterceptor(aspectFactory, method);
    }

    @Override
    protected String getExpression(@Nonnull Method method) {
        return this.getAnnotations(method).getAnnotation(After.class).pointcut();
    }

    @Override
    protected String getArgNames(@Nonnull Method method) {
        return this.getAnnotations(method).getAnnotation(After.class).argNames();
    }
}
