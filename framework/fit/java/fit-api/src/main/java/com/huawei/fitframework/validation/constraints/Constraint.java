/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.constraints;

import com.huawei.fitframework.validation.ConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示约束的注解。
 *
 * @author 邬涨财 w00575064
 * @since 2023-03-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface Constraint {
    /**
     * 表示约束所对应的校验器的类型。
     *
     * @return 表示校验器的类型的 {@link Class}{@code <? extends }{@link ConstraintValidator}{@code <?, ?>>[]}。
     */
    Class<? extends ConstraintValidator<?, ?>>[] value();
}
