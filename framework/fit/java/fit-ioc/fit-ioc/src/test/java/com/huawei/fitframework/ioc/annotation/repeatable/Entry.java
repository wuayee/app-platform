/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.repeatable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 入口注解定义。
 *
 * @author 梁济时 l00815032
 * @since 2022-05-31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@A1
@A2
public @interface Entry {}
