/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为 {@link Fitable} 作用于类型上的实现提供模式定义。
 *
 * @author 梁济时
 * @author 季聿阶
 * @see Alias
 * @see Fitable
 * @since 2020-09-27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Stereotype {
    /** 表示单例模式。 */
    String SINGLETON = "singleton";

    /** 表示原型模式。 */
    String PROTOTYPE = "prototype";

    /**
     * 表示所应用模式的名称。
     *
     * @return 表示模式名称的 {@link String}。
     */
    String value() default "";
}
