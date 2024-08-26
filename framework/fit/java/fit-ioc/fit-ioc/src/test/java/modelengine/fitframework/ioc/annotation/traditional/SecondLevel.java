/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.ioc.annotation.traditional;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示第二层的注解。
 *
 * @author 梁济时
 * @since 2022-05-31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ThirdLevel
public @interface SecondLevel {
    /**
     * 表示注解的值。
     *
     * @return 表示值的 {@link String}。
     */
    @Forward(annotation = ThirdLevel.class, property = "key")
    String name() default "";
}
