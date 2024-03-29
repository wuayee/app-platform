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
 * 定义需要扫描的包。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScanPackages {
    /**
     * 指示需要扫描的包。
     *
     * @return 表示待扫描的包的 {@link String}{@code []}。
     */
    String[] value() default {};
}
