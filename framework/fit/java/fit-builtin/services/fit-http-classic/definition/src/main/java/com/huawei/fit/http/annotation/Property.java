/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.annotation;

import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示参数的详细约束信息。
 *
 * @author 季聿阶 j00559309
 * @since 2023-08-27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Property {
    /**
     * 获取参数的描述信息。
     *
     * @return 表示参数的描述信息的 {@link String}。
     */
    String description() default StringUtils.EMPTY;

    /**
     * 获取参数的样例值。
     *
     * @return 表示参数的样例值的 {@link String}。
     */
    String example() default StringUtils.EMPTY;
}
