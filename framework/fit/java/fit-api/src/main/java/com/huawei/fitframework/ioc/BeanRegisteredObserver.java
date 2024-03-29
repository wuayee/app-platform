/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc;

/**
 * 为Bean被注册提供观察者。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-30
 */
@FunctionalInterface
public interface BeanRegisteredObserver {
    /**
     * 当Bean被注册时被通知。
     *
     * @param metadata 表示新注册的Bean的元数据的 {@link BeanMetadata}。
     */
    void onBeanRegistered(BeanMetadata metadata);
}
