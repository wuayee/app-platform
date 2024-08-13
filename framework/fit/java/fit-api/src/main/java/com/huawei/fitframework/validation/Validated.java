/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示校验的注解。
 * <p>用来标识所要校验的类和参数。若校验的是类，则该类方法的所有带有 {@link com.huawei.fitframework.validation.constraints.Constraint}
 * 参数会被校验；若校验的是参数，则校验该参数对象所有的带有 {@link com.huawei.fitframework.validation.constraints.Constraint} 字段。
 * </p>
 *
 * @author 邬涨财
 * @since 2023-03-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.FIELD})
public @interface Validated {
    /**
     * 表示校验的分组。
     *
     * @return 表示校验分组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] value() default {};
}
