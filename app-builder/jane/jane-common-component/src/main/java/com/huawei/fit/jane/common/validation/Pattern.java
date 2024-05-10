/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.validation;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The pattern
 *
 * @author l00611472
 * @since 2023-12-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(PatternValidator.class)
@Validated
public @interface Pattern {
    String message() default "{javax.validation.constraints.Pattern.message}";

    String regexp();

    Class<?>[] groups() default {};
}