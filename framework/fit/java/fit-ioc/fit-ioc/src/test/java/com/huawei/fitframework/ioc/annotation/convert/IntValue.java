/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示原始注解，保存整数值。
 *
 * @author 梁济时 l00815032
 * @since 2023-01-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntValue {
    /**
     * 表示注解的值。
     *
     * @return 表示注解值的 32 位整数。
     */
    int value() default 0;
}
