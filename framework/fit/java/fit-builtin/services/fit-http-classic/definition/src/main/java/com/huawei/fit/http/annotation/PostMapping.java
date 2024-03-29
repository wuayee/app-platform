/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.annotation;

import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fitframework.annotation.Forward;
import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的 {@link HttpRequestMethod#POST} 请求映射。
 *
 * @author 季聿阶 j00559309
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@RequestMapping(method = HttpRequestMethod.POST)
public @interface PostMapping {
    /**
     * 获取请求映射的路径列表信息。
     *
     * @return 表示请求映射的路径列表信息的 {@link String}{@code []}。
     * @see #path()
     */
    @Forward(annotation = RequestMapping.class, property = "value") String[] value() default {};

    /**
     * 获取请求映射的路径列表信息。
     *
     * @return 表示请求映射的路径列表信息的 {@link String}{@code []}。
     * @see RequestMapping#path()
     */
    @Forward(annotation = RequestMapping.class, property = "path") String[] path() default {};

    /**
     * 获取请求映射的分组信息。
     *
     * @return 表示请求映射的分组信息的 {@link String}。
     * @see RequestMapping#group()
     */
    @Forward(annotation = RequestMapping.class, property = "group") String group() default StringUtils.EMPTY;

    /**
     * 获取请求映射的简短摘要。
     *
     * @return 表示请求映射的简短摘要的 {@link String}。
     * @see RequestMapping#summary()
     */
    @Forward(annotation = RequestMapping.class, property = "summary") String summary() default StringUtils.EMPTY;

    /**
     * 获取请求映射的描述信息。
     *
     * @return 表示请求映射的描述信息的 {@link String}。
     * @see RequestMapping#description()
     */
    @Forward(annotation = RequestMapping.class,
            property = "description") String description() default StringUtils.EMPTY;

    /**
     * 获取请求映射的返回值的描述信息。
     *
     * @return 表示请求映射的返回值的描述信息的 {@link String}。
     * @see RequestMapping#returnDescription()
     */
    @Forward(annotation = RequestMapping.class,
            property = "returnDescription") String returnDescription() default StringUtils.EMPTY;
}
