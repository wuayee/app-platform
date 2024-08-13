/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository.annotation;

import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示返回值的替换信息。
 *
 * @author 季聿阶
 * @since 2024-01-21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ReturnValue {
    /**
     * 表示需要替换的返回值对象属性的路径。
     *
     * @return 表示返回值对象属性的路径的 {@link String}。
     */
    String path() default StringUtils.EMPTY;
}
