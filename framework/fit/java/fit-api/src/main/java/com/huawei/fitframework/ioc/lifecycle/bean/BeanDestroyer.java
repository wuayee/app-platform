/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean;

/**
 * 为Bean提供销毁程序。
 *
 * @author 梁济时 l00815032
 * @since 2022-04-28
 */
@FunctionalInterface
public interface BeanDestroyer {
    /**
     * 销毁指定Bean。
     *
     * @param bean 表示待销毁的Bean的 {@link Object}。
     */
    void destroy(Object bean);
}
