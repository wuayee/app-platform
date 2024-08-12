/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.support;

import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanDestroyer;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanLifecycles;
import com.huawei.fitframework.util.ObjectUtils;

/**
 * 为 {@link BeanFactory} 提供基于生命周期的实现。
 *
 * @author 梁济时
 * @since 2022-04-24
 */
public class PrototypeBeanFactory extends AbstractBeanFactory implements BeanFactory, BeanDestroyer {
    /**
     * 使用Bean的名称、类型和生命周期初始化 {@link PrototypeBeanFactory} 类的新实例。
     *
     * @param lifecycle 表示Bean的生命周期的 {@link BeanLifecycle}。
     * @throws IllegalArgumentException {@code name}、{@code type} 或 {@code lifecycle} 为 {@code null}。
     */
    public PrototypeBeanFactory(BeanLifecycle lifecycle) {
        super(lifecycle);
    }

    @Override
    protected Object get0(Object[] arguments) {
        BeanLifecycle actual = BeanLifecycles.intercept(this.lifecycle());
        Object bean = actual.create(arguments);
        Object decorated = actual.decorate(bean);
        actual.inject(bean);
        actual.initialize(bean);
        return ObjectUtils.cast(decorated);
    }

    @Override
    public void destroy(Object bean) {
        this.lifecycle().destroy(bean);
    }

    @Override
    public String toString() {
        return this.metadata().toString();
    }
}
