/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2023. All rights reserved.
 */

package com.huawei.fitframework.annotation;

import com.huawei.fitframework.util.StringUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于 FIT {@link Genericable 泛服务} 的实现。
 * <p>对于泛服务的实现获取对应的泛服务的唯一标识的优先级如下：（获取的唯一标识不为空白字符串）
 * <ol>
 *     <li>通过 {@link #genericable()} 方法获取一个唯一标识；</li>
 *     <li>通过方法所实现的接口方法上的 {@link Genericable#id()} 获取一个唯一标识；</li>
 *     <li>通过 {@link #generic()} 对应的泛服务接口类上的 {@link Genericable#id()} 获取一个唯一标识。</li>
 * </ol>
 * </p>
 *
 * @author 张群辉 z00544938
 * @author 季聿阶 j00559309
 * @see Genericable
 * @since 2020-01-18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Fitable {
    /**
     * 获取 FIT 泛服务实现对应的泛服务类型的 {@link Class}{@code <?>}。
     *
     * @return 表示该泛服务实现对应的泛服务定义的类型的 {@link Class}{@code <?>}。
     * @see #genericable()
     */
    @Deprecated Class<?> generic() default Object.class;

    /**
     * 获取 FIT 泛服务实现对应的泛服务的唯一标识。
     *
     * @return 表示 FIT 泛服务实现对应的泛服务的唯一标识的 {@link String}。
     */
    String genericable() default StringUtils.EMPTY;

    /**
     * 获取 FIT 泛服务实现的唯一标识。
     *
     * @return 表示泛服务实现的唯一标识的 {@link String}。
     * @see #id()
     */
    @Forward(annotation = Fitable.class, property = "id") String value() default StringUtils.EMPTY;

    /**
     * 获取 FIT 泛服务实现的唯一标识。
     *
     * @return 表示泛服务实现的唯一标识的 {@link String}。
     */
    String id() default StringUtils.EMPTY;
}
