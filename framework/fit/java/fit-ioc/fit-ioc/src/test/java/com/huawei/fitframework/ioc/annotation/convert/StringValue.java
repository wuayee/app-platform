/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.convert;

import com.huawei.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示直接使用的注解，将转发到原始注解 {@link IntValue}，在转发过程将对值进行类型转换。
 *
 * @author 梁济时 l00815032
 * @since 2023-01-28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@IntValue
public @interface StringValue {
    /**
     * 表示字符串类型的值，将转发到 {@link IntValue#value()}。
     *
     * @return 表示注解的值的 {@link String}。
     */
    @Forward(annotation = IntValue.class, converter = AnnotationValueConverter.class)
    String value() default "0";
}
