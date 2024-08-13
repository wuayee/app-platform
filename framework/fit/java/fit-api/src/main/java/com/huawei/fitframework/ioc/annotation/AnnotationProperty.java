/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation;

import java.lang.annotation.Annotation;

/**
 * 为注解提供属性信息。
 *
 * @author 梁济时
 * @since 2022-05-03
 */
public interface AnnotationProperty {
    /**
     * 获取属性所属的注解类型。
     *
     * @return 表示注解类型的 {@link Class}。
     */
    Class<? extends Annotation> annotation();

    /**
     * 获取属性的名称。
     *
     * @return 表示属性名称的 {@link String}。
     */
    String name();
}
