/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 表示携带指定注解的匹配器。
 *
 * @author 季聿阶
 * @since 2022-12-14
 */
public class AnnotationMethodMatcher implements MethodMatcher {
    private final AnnotationMetadataResolver resolver;
    private final Class<? extends Annotation> annotationClass;

    public AnnotationMethodMatcher(Class<? extends Annotation> annotationClass) {
        this.resolver = AnnotationMetadataResolvers.create();
        this.annotationClass = notNull(annotationClass, "The annotation class cannot be null.");
    }

    @Override
    public MatchResult match(@Nonnull Method method) {
        AnnotationMetadata annotations = this.resolver.resolve(method);
        return MatchResult.match(annotations.isAnnotationPresent(this.annotationClass));
    }
}
