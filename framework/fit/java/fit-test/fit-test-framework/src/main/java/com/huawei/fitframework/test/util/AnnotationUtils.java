/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.test.util;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.support.DefaultAnnotationMetadataResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

/**
 * 注解的工具类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-03
 */
public class AnnotationUtils {
    /**
     * 获取指定类型的注解。
     *
     * @param element 表示注解所在的元素的 {@link AnnotatedElement}。
     * @param type 表示指定注解类型的 {@link Class}{@code <}{@link T}{@code >}。
     * @param <T> 表示指定注解类型的 {@link T}。
     * @return 表示指定注解的 {@link Optional}{@code <}{@link T}{@code >}。
     * @throws IllegalArgumentException 当 {@code beanContainer} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code element} 为 {@code null} 时。
     * @throws IllegalArgumentException 当 {@code type} 为 {@code null} 时。
     */
    public static <T extends Annotation> Optional<T> getAnnotation(AnnotatedElement element, Class<T> type) {
        Validation.notNull(element, "The annotated element cannot be null.");
        Validation.notNull(type, "The annotation type cannot be null.");
        AnnotationMetadata annotations = new DefaultAnnotationMetadataResolver().resolve(element);
        if (annotations.isAnnotationPresent(type)) {
            return Optional.of(annotations.getAnnotation(type));
        } else {
            return Optional.empty();
        }
    }
}