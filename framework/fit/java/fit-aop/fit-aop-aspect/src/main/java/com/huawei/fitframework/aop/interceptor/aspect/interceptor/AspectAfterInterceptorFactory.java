/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.interceptor;

import com.huawei.fitframework.aop.annotation.After;
import com.huawei.fitframework.aop.interceptor.AdviceMethodInterceptor;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.BeanFactory;

import java.lang.reflect.Method;

/**
 * 将带有 {@link After} 注解的方法包装成 {@link AdviceMethodInterceptor}。
 *
 * @author 季聿阶 j00559309
 * @author 郭龙飞 gwx900499
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
