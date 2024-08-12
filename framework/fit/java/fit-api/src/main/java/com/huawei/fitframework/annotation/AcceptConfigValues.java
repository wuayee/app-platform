/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示为指定的 Bean 接收配置数据。
 *
 * @author 梁济时
 * @since 2023-01-06
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptConfigValues {
    /**
     * 表示待接收的配置的键。
     *
     * @return 表示配置的键的 {@link String}。
     */
    String value() default StringUtils.EMPTY;
}
