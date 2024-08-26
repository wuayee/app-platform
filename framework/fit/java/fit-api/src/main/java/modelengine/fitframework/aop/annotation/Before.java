/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.aop.annotation;

import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于表示方法执行前的通知。
 *
 * @author 郭龙飞
 * @since 2023-03-07
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Before {
    /**
     * 获取切入点表达式的值。
     *
     * @return 表示切入点表达式的 {@link String}。
     */
    @Forward(annotation = Before.class, property = "pointcut") String value() default "";

    /**
     * 获取切入点表达式的值。
     *
     * @return 表示切入点表达式的 {@link String}。
     */
    String pointcut() default "";

    /**
     * 获取切入点参数所有名称，以逗号分隔符隔开。
     * <p>如果参数只有一个，并且类型是以下特殊类型，该名称可以不写或为空。
     * {@link modelengine.fitframework.aop.JoinPoint}，
     * {@link modelengine.fitframework.aop.ProceedingJoinPoint}，
     * {@link modelengine.fitframework.aop.JoinPoint.StaticPart}</p>
     * <p>如果参数多个，则必须与切入点方法参数名称一致，顺序一致。</p>
     *
     * @return 表示所有参数名称的 {@link String}。
     */
    String argNames() default "";
}
