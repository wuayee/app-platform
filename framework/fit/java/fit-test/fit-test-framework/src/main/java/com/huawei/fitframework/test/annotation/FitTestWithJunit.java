/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.test.annotation;

import com.huawei.fitframework.test.adapter.north.junit5.FitExtension;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持 Fit 测试框架的指定注解。
 * <p>当前注解支持 Junit4 和 Junit5 使用。</p>
 * <p> Junit4 案例：</p>
 * <pre>
 *  1:   @RunWith(FitRunner.class)
 *  2:   @FitTestWithJunit(classes = {xxx.class})
 *  3:   public class DemoTest {
 *  4:   }
 * </pre>
 * <p> Junit5 案例：</p>
 * <pre>
 *  1:   @FitTestWithJunit(classes = {xxx.class})
 *  2:   public class DemoTest {
 *  3:   }
 * </pre>
 *
 * @author 邬涨财 w00575064
 * @since 2023-01-17
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(FitExtension.class)
public @interface FitTestWithJunit {
    /**
     * 需要注入到容器的组件类型数组。
     *
     * @return 表示需要注入到容器的组件类型数组的 {@link Class}{@code <?>[]}。
     */
    Class<?>[] classes() default {};
}
