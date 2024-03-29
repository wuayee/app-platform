/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc;

/**
 * 为IoC容器提供工厂Bean的定义。
 *
 * <p>将通过接口中泛型参数所提供的类型进行匹配。</p>
 *
 * @param <T> 表示Bean的类型。
 * @author 季聿阶 j00559309
 * @since 2022-05-31
 */
public interface BeanSupplier<T> {
    /**
     * 获取Bean实例。
     *
     * @return 表示Bean实例的 {@link Object}。
     */
    T get();
}
