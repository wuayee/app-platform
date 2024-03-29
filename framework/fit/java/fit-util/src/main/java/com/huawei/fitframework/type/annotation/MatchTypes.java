/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package com.huawei.fitframework.type.annotation;

import com.huawei.fitframework.type.TypeMatcher;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;

/**
 * 为匹配判定程序所支持的对象类型的类型提供定义。
 *
 * @author 梁济时 l00815032
 * @since 2020-10-29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchTypes {
    /**
     * 表示当前类型的类型。
     *
     * @return 表示对象类型的类型的 {@link Type}。
     */
    Class<? extends Type> current();

    /**
     * 表示所期望的类型的类型。
     *
     * @return 表示所期望类型的类型的 {@link Type}。
     */
    Class<? extends Type> expected();

    /**
     * 表示用以实例化类型匹配判定程序的工厂的类型。
     * <p>工厂需具备默认构造方法。</p>
     *
     * @return 表示类型匹配程序工厂的类型的 {@link TypeMatcher.Factory}。
     */
    Class<? extends TypeMatcher.Factory> factory();
}
