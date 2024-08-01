/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.test.annotation;

import com.huawei.fitframework.annotation.Forward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于 Mvc 测试。
 *
 * @author 易文渊
 * @since 2024-07-21
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@FitTestWithJunit
@EnableMockMvc
public @interface MvcTest {
    /**
     * 需要注入到容器的组件类型数组。
     *
     * @return 表示需要注入到容器的组件类型数组的 {@link Class}{@code <?>[]}。
     */
    @Forward(annotation = FitTestWithJunit.class, property = "includeClasses") Class<?>[] classes() default {};
}
