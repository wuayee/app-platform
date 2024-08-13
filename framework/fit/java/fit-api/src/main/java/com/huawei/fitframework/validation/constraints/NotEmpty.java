/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.validation.constraints;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.validators.NotEmptyValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示校验元素是否为空的注解。
 *
 * @author 邬涨财
 * @since 2023-03-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(NotEmptyValidator.class)
@Validated
public @interface NotEmpty {
    /**
     * 表示校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message() default "must not be empty";

    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups() default {};
}
