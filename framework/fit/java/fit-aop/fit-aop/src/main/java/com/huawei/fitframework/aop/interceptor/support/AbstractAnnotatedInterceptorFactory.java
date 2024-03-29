/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.aop.interceptor.MethodInterceptorFactory;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 表示通过方法注解来生成方法拦截器的工厂。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-14
 */
public abstract class AbstractAnnotatedInterceptorFactory implements MethodInterceptorFactory {
    private final Class<? extends Annotation> annotationClass;

    protected AbstractAnnotatedInterceptorFactory(Class<? extends Annotation> annotationClass) {
        this.annotationClass = notNull(annotationClass, "The annotation class cannot be null.");
    }

    @Override
    public boolean isInterceptMethod(@Nonnull Method method) {
        return this.getAnnotations(method).isAnnotationPresent(this.annotationClass);
    }

    /**
     * 获取指定方法的注解元数据信息。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示指定方法的注解元数据信息的 {@link AnnotationMetadata}。
     */
    protected AnnotationMetadata getAnnotations(@Nonnull Method method) {
        return AnnotationMetadataResolvers.create().resolve(method);
    }
}
