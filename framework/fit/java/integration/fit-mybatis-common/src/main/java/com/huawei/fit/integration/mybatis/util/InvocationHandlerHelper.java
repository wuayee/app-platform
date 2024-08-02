/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.integration.mybatis.util;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.integration.mybatis.MapperInvocationHandler;
import com.huawei.fitframework.aop.proxy.FitProxy;

import sun.reflect.ReflectionFactory;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.RandomString;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * 为 {@link java.lang.reflect.InvocationHandler} 提供统一的工具类。
 *
 * @author 季聿阶
 * @since 2024-08-01
 */
public class InvocationHandlerHelper {
    private static final String BYTE_BUDDY_CLASSNAME = "net.bytebuddy.ByteBuddy";

    /**
     * 通过 JDK 获取动态代理。
     *
     * @param mapperClass 表示待代理的类型的 {@link Class}{@code <}{@link M}{@code >}。
     * @param handler 表示 {@link java.lang.reflect.InvocationHandler} 的实例的 {@link MapperInvocationHandler}。
     * @param <M> 表示代理类型的 {@link M}。
     * @return 表示动态代理对象的 {@link M}。
     */
    public static <M> M proxyByJdk(Class<M> mapperClass, MapperInvocationHandler handler) {
        return cast(Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[] {mapperClass}, handler));
    }

    /**
     * 通过 ByteBuddy 获取动态代理。
     *
     * @param mapperClass 表示待代理的类型的 {@link Class}{@code <}{@link M}{@code >}。
     * @param handler 表示 {@link java.lang.reflect.InvocationHandler} 的实例的 {@link MapperInvocationHandler}。
     * @param <M> 表示代理类型的 {@link M}。
     * @return 表示动态代理对象的 {@link M}。
     */
    public static <M> M proxyByByteBuddy(Class<M> mapperClass, MapperInvocationHandler handler) {
        Class<?> proxiedClass = new ByteBuddy().subclass(mapperClass)
                .implement(FitProxy.class)
                .name(mapperClass.getName() + "$$Fit$ByteBuddy$$" + RandomString.make(8))
                .method(ElementMatchers.isMethod())
                .intercept(InvocationHandlerAdapter.of(handler))
                .make()
                .load(mapperClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        Constructor<Object> objectConstructor = getObjectConstructor();
        ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();
        Constructor<M> constructor =
                cast(reflectionFactory.newConstructorForSerialization(proxiedClass, objectConstructor));
        try {
            return constructor.newInstance();
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

    /**
     * 检查 ByteBuddy 是否可用。
     *
     * @return 如果 ByteBuddy 可用，则返回 {@code true}；否则返回 {@code false}。
     */
    public static boolean isByteBuddyAvailable() {
        try {
            Class.forName(BYTE_BUDDY_CLASSNAME, false, MapperInvocationHandler.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
