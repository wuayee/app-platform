/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy;

import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * 表示 AOP 代理的工厂。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-04
 */
public interface AopProxyFactory {
    /**
     * 判断指定类型是否可以创建 AOP 代理。
     *
     * @param targetClass 表示指定类型的 {@link Class}{@code <}{@link Object}{@code >}。
     * @return 如果可以创建，则返回 {@code true}，否则，返回 {@code false}。
     */
    boolean support(Class<?> targetClass);

    /**
     * 通过指定拦截事件支持信息创建 AOP 代理。
     *
     * @param support 表示指定的拦截事件支持信息的 {@link InterceptSupport}。
     * @return 表示创建出来的 AOP 代理的 {@link Object}。
     */
    Object createProxy(InterceptSupport support);

    /**
     * 获取所有排序好的的 {@link AopProxyFactory}。
     *
     * @return 表示获取到的所有排序好的的 {@link List}{@code <}{@link AopProxyFactory}{@code >}。
     */
    static List<AopProxyFactory> all() {
        ServiceLoader<AopProxyFactory> loader =
                ServiceLoader.load(AopProxyFactory.class, AopProxyFactory.class.getClassLoader());
        List<AopProxyFactory> factories = new ArrayList<>();
        loader.forEach(factories::add);
        factories.sort((f1, f2) -> {
            AnnotationMetadataResolver resolver = AnnotationMetadataResolvers.create();
            AnnotationMetadata annotations = resolver.resolve(f1.getClass());
            int order1 =
                    Optional.ofNullable(annotations.getAnnotation(Order.class)).map(Order::value).orElse(Order.MEDIUM);
            annotations = resolver.resolve(f2.getClass());
            int order2 =
                    Optional.ofNullable(annotations.getAnnotation(Order.class)).map(Order::value).orElse(Order.MEDIUM);
            return Integer.compare(order1, order2);
        });
        return factories;
    }
}
