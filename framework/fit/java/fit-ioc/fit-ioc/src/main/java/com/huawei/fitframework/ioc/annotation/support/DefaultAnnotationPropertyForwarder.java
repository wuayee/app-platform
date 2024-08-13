/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.support;

import com.huawei.fitframework.annotation.Forward;
import com.huawei.fitframework.ioc.annotation.AnnotationProperties;
import com.huawei.fitframework.ioc.annotation.AnnotationProperty;
import com.huawei.fitframework.ioc.annotation.AnnotationPropertyForward;
import com.huawei.fitframework.ioc.annotation.AnnotationPropertyForwarder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

/**
 * 为 {@link AnnotationPropertyForwarder} 提供默认实现。
 *
 * @author 梁济时
 * @since 2022-06-15
 */
public class DefaultAnnotationPropertyForwarder implements AnnotationPropertyForwarder {
    @Override
    public Optional<AnnotationPropertyForward> forward(Method propertyMethod) {
        Forward forward = propertyMethod.getAnnotation(Forward.class);
        if (forward == null) {
            return Optional.empty();
        }
        Class<? extends Annotation> targetAnnotation = Optional.of(forward.annotation())
                .filter(annotation -> !Objects.equals(annotation, Annotation.class))
                .orElseGet(() -> ObjectUtils.cast(propertyMethod.getDeclaringClass()));
        String targetProperty = Optional.of(forward.property())
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .orElseGet(propertyMethod::getName);
        AnnotationProperty target = AnnotationProperties.create(targetAnnotation, targetProperty);
        return Optional.of(new DefaultAnnotationPropertyForward(target, forward.converter()));
    }
}
