/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明 Bean 的依赖。
 *
 * @author 梁济时 l00815032
 * @since 2022-11-29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DependsOn {
    /**
     * 指示所依赖的 Bean 的名称。
     *
     * @return 表示所依赖 Bean 的名称的 {@link String}{@code []}。
     */
    String[] value() default {};
}
