/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于工具方法的定义。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ToolMethod {
    /**
     * 获取工具方法的名。
     *
     * @return 表示工具方法名的 {@link String}。
     */
    String name() default "";

    /**
     * 获取工具方法的描述信息。
     *
     * @return 表示工具方法的描述信息的 {@link String}。
     */
    String description() default "";
}
