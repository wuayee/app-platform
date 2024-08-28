/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.annotation;

import modelengine.fit.http.server.handler.Source;
import modelengine.fitframework.annotation.Forward;
import modelengine.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的请求映射中的 Cookie 参数。
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@RequestParam(in = Source.COOKIE)
public @interface RequestCookie {
    /**
     * 获取 Cookie 参数的名字。
     *
     * @return 表示 Cookie 参数名字的 {@link String}。
     * @see #name()
     */
    @Forward(annotation = RequestCookie.class, property = "name") String value() default StringUtils.EMPTY;

    /**
     * 获取 Cookie 参数的名字。
     *
     * @return 表示 Cookie 参数名字的 {@link String}。
     */
    @Forward(annotation = RequestParam.class, property = "name") String name() default StringUtils.EMPTY;

    /**
     * 获取 Cookie 参数是否为必须的标志。
     *
     * @return 如果 Cookie 参数必须存在，则返回 {@code true}，否则，返回 {@code false}。
     */
    @Forward(annotation = RequestParam.class, property = "required") boolean required() default true;

    /**
     * 获取 Cookie 参数的默认值。
     *
     * @return 表示 Cookie 参数的默认值的 {@link String}。
     */
    @Forward(annotation = RequestParam.class,
            property = "defaultValue") String defaultValue() default DefaultValue.VALUE;
}
