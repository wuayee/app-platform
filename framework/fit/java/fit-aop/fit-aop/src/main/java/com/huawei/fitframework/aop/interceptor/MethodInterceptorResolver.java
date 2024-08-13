/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor;

import com.huawei.fitframework.ioc.BeanMetadata;

import java.util.List;

/**
 * 表示方法拦截器的解析器。
 * <p>用于从用户的定义中解析出对应的方法拦截器。</p>
 *
 * @author 季聿阶
 * @since 2022-05-06
 */
public interface MethodInterceptorResolver {
    /**
     * 检查是否需要排除指定 Bean，即不对指定 Bean 执行 AOP。
     *
     * @param metadata 表示待检查的 Bean 的元数据的 {@link BeanMetadata}。
     * @return 若需要排除，则为 {@code true}，否则为 {@code false}。
     */
    boolean eliminate(BeanMetadata metadata);

    /**
     * 从用户定义中解析指定 Bean 的方法拦截器。
     *
     * @param beanMetadata 表示指定 Bean 的元数据的 {@link BeanMetadata}。
     * @param bean 表示指定 Bean 的 {@link Object}。
     * @return 表示解析得到的方法拦截器列表的 {@link List}{@code <}{@link MethodInterceptor}{@code >}。
     */
    List<MethodInterceptor> resolve(BeanMetadata beanMetadata, Object bean);
}
