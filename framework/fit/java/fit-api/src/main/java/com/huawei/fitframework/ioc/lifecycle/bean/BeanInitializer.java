/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean;

/**
 * 为Bean提供初始化程序。
 *
 * @author 梁济时 l00815032
 * @since 2022-04-28
 */
@FunctionalInterface
public interface BeanInitializer {
    /**
     * 初始化指定的Bean实例。
     *
     * @param bean 表示待初始化的Bean实例的 {@link Object}。
     */
    void initialize(Object bean);
}
