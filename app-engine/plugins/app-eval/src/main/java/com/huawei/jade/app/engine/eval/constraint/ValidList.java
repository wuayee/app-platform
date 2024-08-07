/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.constraint;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.constraints.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示校验列表中全部元素是否是合法唯一标识。
 *
 * @author 何嘉斌
 * @since 2024-07-24
 */
@Constraint(ValidListValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Validated
public @interface ValidList {
    /**
     * 表示校验元素的下限值。
     *
     * @return 表示校验元素的下限值的 {@code long}。
     */
    long min();

    /**
     * 表示校验元素的上限值。
     *
     * @return 表示校验元素的上限值的 {@code long}。
     */
    long max();

    /**
     * 表示校验失败的信息。
     *
     * @return 表示校验失败的信息的 {@link String}。
     */
    String message() default "List element is invalid.";

    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] groups() default {};
}
