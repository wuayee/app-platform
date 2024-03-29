/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示别名信息。
 * <p>为 {@link Fitable} 作用于类型上的实现提供别名定义，或是其他场景也可以复用此注解。</p>
 *
 * @author 梁济时 l00815032
 * @author 季聿阶 j00559309
 * @see Stereotype
 * @see Fitable
 * @since 2020-12-14
 */
@Documented
@Repeatable(Aliases.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface Alias {
    /**
     * 获取别名。
     *
     * @return 表示别名的 {@link String}。
     */
    String value() default StringUtils.EMPTY;
}
