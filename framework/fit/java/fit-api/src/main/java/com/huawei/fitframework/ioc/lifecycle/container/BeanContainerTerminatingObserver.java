/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.container;

import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanFactoryOrderComparator;

/**
 * 为 Bean 容器被终结前提供观察者。
 *
 * @author 梁济时
 * @since 2022-06-09
 */
@FunctionalInterface
public interface BeanContainerTerminatingObserver {
    /**
     * 当 Bean 容器被终结时调用的方法。
     *
     * @param container 表示正在被终结的 Bean 容器的 {@link BeanContainer}。
     */
    void onBeanContainerTerminating(BeanContainer container);

    /**
     * 通知所有容器中所有实现了 {@link BeanContainerTerminatingObserver} 接口的 Bean。
     *
     * @param container 表示被终结前的 Bean 容器的 {@link BeanContainer}。
     */
    static void notify(BeanContainer container) {
        if (container == null) {
            return;
        }
        container.all(BeanContainerTerminatingObserver.class)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::<BeanContainerTerminatingObserver>get)
                .forEach(observer -> observer.onBeanContainerTerminating(container));
    }
}
