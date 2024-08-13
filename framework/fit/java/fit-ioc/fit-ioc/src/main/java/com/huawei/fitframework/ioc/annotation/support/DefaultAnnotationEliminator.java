/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.support;

import com.huawei.fitframework.ioc.annotation.AnnotationEliminator;

import java.lang.annotation.Annotation;

/**
 * 为 {@link AnnotationEliminator} 提供默认实现。
 * <p>将排除 {@link java.lang.annotation.Retention}、{@link java.lang.annotation.Documented}、
 * {@link java.lang.annotation.Target}、{@link java.lang.annotation.Inherited} 注解。</p>
 *
 * @author 梁济时
 * @since 2022-05-03
 */
public class DefaultAnnotationEliminator implements AnnotationEliminator {
    @Override
    public boolean eliminate(Annotation annotation) {
        return annotation.annotationType().getName().startsWith("java.lang.");
    }
}
