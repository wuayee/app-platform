/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean.support;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanCreationException;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanCreator;

/**
 * 为 {@link BeanCreator} 提供基于直接对象的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-04-28
 */
public class DirectBeanCreator implements BeanCreator {
    private final Object bean;

    /**
     * 使用直接对象初始化 {@link DirectBeanCreator} 类的新实例。
     *
     * @param bean 表示直接对象的 {@link Object}。
     * @throws IllegalArgumentException {@code bean} 为 {@code null}。
     */
    public DirectBeanCreator(Object bean) {
        this.bean = Validation.notNull(bean, "The direct bean of a creator cannot be null.");
    }

    @Override
    public Object create(Object[] arguments) {
        if (arguments != null && arguments.length > 0) {
            throw new BeanCreationException("Cannot instantiate direct bean with initial arguments.");
        } else {
            return this.bean;
        }
    }
}
