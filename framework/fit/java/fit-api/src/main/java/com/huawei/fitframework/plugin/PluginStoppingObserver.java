/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.plugin;

import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.ioc.BeanFactoryOrderComparator;

/**
 * 表示插件停止之前的观察者。
 *
 * @author 季聿阶 j00559309
 * @since 2023-08-07
 */
@FunctionalInterface
public interface PluginStoppingObserver {
    /**
     * 当指定插件停止开始之前调用的方法。
     *
     * @param plugin 表示指定插件的 {@link Plugin}。
     */
    void onPluginStopping(Plugin plugin);

    /**
     * 通知所有容器中所有实现了 {@link PluginStoppingObserver} 接口的 Bean。
     *
     * @param plugin 表示准备停止的插件的 {@link Plugin}。
     */
    static void notify(Plugin plugin) {
        if (plugin == null) {
            return;
        }
        plugin.container()
                .all(PluginStoppingObserver.class)
                .stream()
                .sorted(BeanFactoryOrderComparator.INSTANCE)
                .map(BeanFactory::<PluginStoppingObserver>get)
                .forEach(observer -> observer.onPluginStopping(plugin));
    }
}
