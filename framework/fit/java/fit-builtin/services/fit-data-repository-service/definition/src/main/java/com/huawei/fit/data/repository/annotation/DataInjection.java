/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示需要进行出入参数据替换的注解。
 *
 * @author 季聿阶 j00559309
 * @since 2024-01-21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DataInjection {
    /**
     * 表示需要替换的入参信息。
     *
     * @return 表示入参信息的 {@link Parameter}{@code []}。
     */
    Parameter[] parameters() default {};

    /**
     * 表示需要替换的返回值信息。
     *
     * @return 表示返回值信息的 {@link ReturnValue}{@code []}。
     */
    ReturnValue[] returnValue() default {};
}
