/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.test;

import com.huawei.fitframework.annotation.Forward;
import com.huawei.fitframework.aop.annotation.After;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 *
 * @author bWX1068551
 * @since 2023-04-04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@After
public @interface After1 {
    @Forward(annotation = After.class, property = "value") String value() default "";

    @Forward(annotation = After.class, property = "argNames") String argNames() default "";
}
