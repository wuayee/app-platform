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
 * 为 {@link Fitable} 提供降级的默认定义。
 *
 * @author 季聿阶 j00559309
 * @see Fitable
 * @since 2022-06-09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Degradation {
    /**
     * 获取降级后的实现的别名。
     *
     * @return 表示降级后的实现的别名的 {@link String}。
     */
    String to();
}
