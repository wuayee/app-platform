/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示适配器。
 *
 * @author 梁济时
 * @author 季聿阶
 * @since 2022-11-15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Adapter {
    /**
     * 获取使用的适配器的类型。
     *
     * @return 表示适配器类型的 {@link Class}。
     */
    Class<?> value();
}
