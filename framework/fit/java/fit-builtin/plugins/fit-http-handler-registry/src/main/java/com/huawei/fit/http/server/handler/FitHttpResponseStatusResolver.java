/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.ResponseStatus;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 表示从 {@link ResponseStatus} 注解中获取响应状态的解析器。
 *
 * @author 季聿阶
 * @since 2023-01-11
 */
public class FitHttpResponseStatusResolver implements HttpResponseStatusResolver {
    private final BeanContainer container;

    FitHttpResponseStatusResolver(BeanContainer container) {
        this.container = notNull(container, "The bean container cannot be null.");
    }

    @Override
    public Optional<HttpResponseStatus> resolve(Method method) {
        AnnotationMetadataResolver annotationResolver = this.container.runtime().resolverOfAnnotations();
        AnnotationMetadata annotations = annotationResolver.resolve(method);
        if (annotations.isAnnotationPresent(ResponseStatus.class)) {
            return Optional.of(annotations.getAnnotation(ResponseStatus.class).code());
        }
        annotations = annotationResolver.resolve(method.getDeclaringClass());
        if (annotations.isAnnotationPresent(ResponseStatus.class)) {
            return Optional.of(annotations.getAnnotation(ResponseStatus.class).code());
        }
        return Optional.empty();
    }
}
