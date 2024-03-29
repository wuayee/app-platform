/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.support;

import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 为 {@link AnnotationMetadata} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-03
 */
class DefaultAnnotationMetadata implements AnnotationMetadata {
    private final List<Annotation> annotations;

    /**
     * 使用注解的列表初始化 {@link DefaultAnnotationMetadata} 类的新实例。
     *
     * @param annotations 表示定义的注解的集合的 {@link List}{@code <}{@link Annotation}{@code >}。
     */
    DefaultAnnotationMetadata(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> type) {
        return this.getAnnotation(type) != null;
    }

    @Override
    public Annotation[] getAnnotations() {
        return this.annotations.toArray(new Annotation[0]);
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        List<Annotation> matchedAnnotations =
                this.annotations.stream().filter(type::isInstance).collect(Collectors.toList());
        if (matchedAnnotations.size() == 1) {
            return ObjectUtils.cast(matchedAnnotations.get(0));
        } else {
            return null;
        }
    }

    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> type) {
        List<T> matchedAnnotations = this.annotations.stream()
                .filter(type::isInstance)
                .map(ObjectUtils::<T>cast)
                .collect(Collectors.toList());
        T[] array = ObjectUtils.cast(Array.newInstance(type, 0));
        return matchedAnnotations.toArray(array);
    }
}
