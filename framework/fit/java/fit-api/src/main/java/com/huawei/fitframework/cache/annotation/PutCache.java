/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.cache.annotation;

import com.huawei.fitframework.annotation.Forward;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示将对象结果设置为对应的缓存。
 *
 * @author 季聿阶 j00559309
 * @since 2022-12-13
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PutCache {
    /**
     * 获取缓存实例的名字列表。
     *
     * @return 表示缓存实例名字列表的 {@link String}{@code []}。
     * @see #name()
     */
    @Forward(annotation = PutCache.class, property = "name") String[] value() default {};

    /**
     * 获取缓存实例的名字列表。
     *
     * @return 表示缓存实例名字列表的 {@link String}{@code []}。
     */
    String[] name() default {};

    /**
     * 获取缓存对象的键的样式。
     *
     * @return 表示缓存对象键的样式的 {@link String}。
     * @see Cacheable#key()
     */
    String key() default "";
}
