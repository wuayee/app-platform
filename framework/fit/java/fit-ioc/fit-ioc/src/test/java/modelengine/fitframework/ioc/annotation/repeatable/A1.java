/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc.annotation.repeatable;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示定义了 {@link Value} 的定义注解。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Value
public @interface A1 {
    @Forward(annotation = Value.class, property = "value")
    String a1() default "";
}
