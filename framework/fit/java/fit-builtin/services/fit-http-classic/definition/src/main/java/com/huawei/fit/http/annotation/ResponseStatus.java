/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.annotation;

import com.huawei.fit.http.protocol.HttpResponseStatus;
import modelengine.fitframework.annotation.Forward;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示 REST 接口的响应映射中的状态码。
 *
 * @author 季聿阶
 * @since 2023-01-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ResponseStatus {
    /**
     * 获取响应状态码。
     *
     * @return 表示响应状态码的 {@link HttpResponseStatus}。
     * @see #code()
     */
    @Forward(annotation = ResponseStatus.class,
            property = "code") HttpResponseStatus value() default HttpResponseStatus.OK;

    /**
     * 获取响应状态码。
     *
     * @return 表示响应状态码的 {@link HttpResponseStatus}。
     */
    HttpResponseStatus code() default HttpResponseStatus.OK;
}
