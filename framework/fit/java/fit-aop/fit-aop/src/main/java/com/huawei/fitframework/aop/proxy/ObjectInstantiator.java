/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.proxy;

/**
 * 任意对象生成器。
 *
 * @param <T> 表示待实例化对象的类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2022-05-10
 */
public interface ObjectInstantiator<T> {
    /**
     * 实例化一个对象。
     *
     * @return 表示待实例化的对象的 {@link T}。
     */
    T newInstance();
}
