/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.async;

import com.huawei.fitframework.annotation.Asynchronous;
import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.aop.interceptor.support.DefaultMatchResult;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolvers;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 表示 {@link Asynchronous} 注解定义异步执行方法的方法匹配器。
 *
 * @author 季聿阶 j00559309
 * @since 2022-11-13
 */
public class AsyncMethodMatcher implements MethodMatcher {
    private final AnnotationMetadataResolver resolver;
    private final String asyncExecutorName;

    /**
     * 创建异步执行方法的方法匹配器。
     *
     * @param asyncExecutorName 表示异步执行方法器名的 {@link String}。
     */
    public AsyncMethodMatcher(String asyncExecutorName) {
        this.resolver = AnnotationMetadataResolvers.create();
        if (StringUtils.isBlank(asyncExecutorName)) {
            this.asyncExecutorName = StringUtils.EMPTY;
        } else {
            this.asyncExecutorName = asyncExecutorName;
        }
    }

    @Override
    public MatchResult match(@Nonnull Method method) {
        boolean matches = this.getAsyncExecutorName(method)
                .filter(name -> Objects.equals(name, this.asyncExecutorName))
                .isPresent();
        return new DefaultMatchResult(matches);
    }

    private Optional<String> getAsyncExecutorName(Method method) {
        AnnotationMetadata annotations = this.resolver.resolve(method);
        if (annotations.isAnnotationPresent(Asynchronous.class)) {
            return Optional.of(annotations.getAnnotation(Asynchronous.class).executor());
        }
        annotations = this.resolver.resolve(method.getDeclaringClass());
        if (annotations.isAnnotationPresent(Asynchronous.class)) {
            return Optional.of(annotations.getAnnotation(Asynchronous.class).executor());
        }
        return Optional.empty();
    }
}
