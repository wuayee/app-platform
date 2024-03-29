/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.annotation;

import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示入参的替换信息。
 *
 * @author 季聿阶 j00559309
 * @since 2024-01-21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    /**
     * 表示需要替换的入参的下标。
     *
     * @return 表示入参下标的 {@code int}。
     */
    int index();

    /**
     * 表示需要替换的入参对象属性的路径。
     *
     * @return 表示入参对象属性的路径的 {@link String}。
     */
    String path() default StringUtils.EMPTY;
}
