/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.validation.constraints;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.validators.NotBlankValidator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示校验元素是否为空白的注解。
 *
 * @author 邬涨财 w00575064
 * @since 2023-03-08
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(NotBlankValidator.class)
@Validated
public @interface NotBlank {
    /**
     * 表示校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message() default "must not be blank";

    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups() default {};
}
