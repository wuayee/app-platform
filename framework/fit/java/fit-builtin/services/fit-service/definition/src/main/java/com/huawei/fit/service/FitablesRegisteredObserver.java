/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.service;

import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanFactoryOrderComparator;

/**
 * 表示全部的服务实现都已经注册完毕的事件。
 *
 * @author 季聿阶
 * @since 2022-09-12
 */
@FunctionalInterface
public interface FitablesRegisteredObserver {
    /**
     * 当所有的服务实现都已经注册完毕时，调用的方法。
     */
    void onFitablesRegistered();

    /**
     * 通知所有容器中所有实现了 {@link FitablesRegisteredObserver} 接口的 Bean。
     *
     * @param container 表示已初始化完成的 Bean 容器的 {@link BeanContainer}。
     */
    static void notify(BeanContainer container) {
        if (container == null) {
            return;
        }
        container.all(FitablesRegisteredObserver.class)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::<FitablesRegisteredObserver>get)
                .forEach(FitablesRegisteredObserver::onFitablesRegistered);
    }
}
