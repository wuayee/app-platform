/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy.support;

import com.huawei.fitframework.aop.proxy.ObjectInstantiator;
import com.huawei.fitframework.util.ObjectUtils;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 通过 {@link ReflectionFactory} 实现的任意对象生成器。
 * <p>该对象生成器会创建一个指定类型的无参构造方法，然后调用该构造方法生成实例。因为构造方法是动态生成的，
 * 因此不会调用任何当前已存在的构造方法，就算指定类型原本就有无参构造方法，也不会调用。</p>
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-10
 */
public class ReflectionFactoryInstantiator<T> implements ObjectInstantiator<T> {
    private final Constructor<T> constructor;

    /**
     * 使用指定类型实例化一个 {@link ReflectionFactoryInstantiator}。
     *
     * @param type 表示指定类型的 {@link Class}{@code <}{@link T}{@code >}。
     */
    public ReflectionFactoryInstantiator(Class<T> type) {
        Constructor<Object> objectConstructor = getObjectConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        this.constructor = ObjectUtils.cast(reflectionFactory.newConstructorForSerialization(type, objectConstructor));
    }

    @Override
    public T newInstance() {
        try {
            return this.constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Constructor<Object> getObjectConstructor() {
        try {
            return Object.class.getConstructor((Class<?>[]) null);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
