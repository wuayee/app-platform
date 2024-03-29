/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.annotation;

import com.huawei.fit.http.server.handler.Source;
import com.huawei.fitframework.annotation.Forward;
import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的请求映射中的查询参数。
 *
 * @author 邬涨财 w00575064
 * @since 2023-11-20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@RequestParam(in = Source.QUERY)
public @interface RequestQuery {
    /**
     * 获取查询参数的名字。
     *
     * @return 表示查询参数名字的 {@link String}。
     * @see #name()
     */
    @Forward(annotation = RequestQuery.class, property = "name") String value() default StringUtils.EMPTY;

    /**
     * 获取查询参数的名字。
     *
     * @return 表示查询参数名字的 {@link String}。
     */
    @Forward(annotation = RequestParam.class, property = "name") String name() default StringUtils.EMPTY;

    /**
     * 获取查询参数是否为必须的标志。
     *
     * @return 如果查询参数必须存在，则返回 {@code true}，否则，返回 {@code false}。
     */
    @Forward(annotation = RequestParam.class, property = "required") boolean required() default true;

    /**
     * 获取查询参数的默认值。
     *
     * @return 表示查询参数的默认值的 {@link String}。
     */
    @Forward(annotation = RequestParam.class,
            property = "defaultValue") String defaultValue() default DefaultValue.VALUE;
}
