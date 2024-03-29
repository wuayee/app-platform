/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.test.annotation;

import com.huawei.fitframework.annotation.Fit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于测试类字段的 Mock。
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-29
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Fit
public @interface Mocked {}
