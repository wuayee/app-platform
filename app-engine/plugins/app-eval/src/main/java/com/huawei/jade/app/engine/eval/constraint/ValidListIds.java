/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.constraint;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示校验列表中全部元素是否是合法ID。
 *
 * @author 何嘉斌
 * @since 2024-07-24
 */
@Constraint(ValidListIdsValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Validated
public @interface ValidListIds {
    String message() default "The dataset id is invalid.";
    Class<?>[] groups() default {};
    Class<? extends Long>[] payload() default {};
}
