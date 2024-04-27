/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStoppingObserver;
import com.huawei.jade.store.ToolFactory;
import com.huawei.jade.store.repository.ToolFactoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示观察所有插件启动的观察者的实现。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
@Component
public class ToolExecutionStarter implements PluginStartedObserver, PluginStoppingObserver {
    private final ToolFactoryRepository toolFactoryRepository;

    ToolExecutionStarter(ToolFactoryRepository toolFactoryRepository) {
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repo cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        List<ToolFactory> factories = plugin.container()
                .factories(ToolFactory.class)
                .stream()
                .map(BeanFactory::<ToolFactory>get)
                .collect(Collectors.toList());
        for (ToolFactory toolFactory : factories) {
            this.toolFactoryRepository.register(toolFactory);
        }
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        List<ToolFactory> factories = plugin.container()
                .factories(ToolFactory.class)
                .stream()
                .map(BeanFactory::<ToolFactory>get)
                .collect(Collectors.toList());
        for (ToolFactory toolFactory : factories) {
            this.toolFactoryRepository.unregister(toolFactory);
        }
    }
}
