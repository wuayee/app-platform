/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation;

import java.lang.annotation.Annotation;

/**
 * 表示约束的校验器类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-03-08
 */
public interface ConstraintValidator<A extends Annotation, T> {
    /**
     * 校验器类的初始方法。每一个校验器对象都只会调用一次。
     *
     * @param constraintAnnotation 表示约束注解的 {@link A}。
     */
    default void initialize(A constraintAnnotation) {}

    /**
     * 校验对象是否合法。
     *
     * @param value 表示需要校验的目标对象的 {@link T}。
     * @return 表示校验对象是否合法的 {@code boolean}。
     */
    boolean isValid(T value);
}
