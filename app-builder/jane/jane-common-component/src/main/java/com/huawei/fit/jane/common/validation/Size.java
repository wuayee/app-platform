/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.common.validation;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The Size
 *
 * @author 刘信宏
 * @since 2023-12-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(SizeValidator.class)
@Validated
public @interface Size {
    String message() default "{javax.validation.constraints.Size.message}";

    Class<?>[] groups() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;
}
