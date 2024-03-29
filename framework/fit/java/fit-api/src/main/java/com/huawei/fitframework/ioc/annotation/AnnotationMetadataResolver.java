/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation;

import java.lang.reflect.AnnotatedElement;

/**
 * 为注解的元数据提供解析程序。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-04
 */
@FunctionalInterface
public interface AnnotationMetadataResolver {
    /**
     * 为指定的类型解析注解元数据。
     *
     * @param element 表示待解析的可注解元素的 {@link AnnotatedElement}。
     * @return 表示从对象类型解析到的注解元数据的 {@link AnnotationMetadata}。
     * @throws IllegalArgumentException {@code objectClass} 为 {@code null}。
     */
    AnnotationMetadata resolve(AnnotatedElement element);
}
