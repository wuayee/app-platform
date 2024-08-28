/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package modelengine.fitframework.annotation;

import modelengine.fitframework.ioc.Condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为 Bean 的加载提供条件。
 *
 * @author 梁济时
 * @since 2022-11-14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Conditional {
    /**
     * 获取 Bean 生效条件的类型。
     *
     * @return 表示 Bean 生效条件类型的 {@link Class}。
     */
    Class<? extends Condition>[] value() default {};
}
