/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;
import modelengine.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.carver.tool.repository.ToolFactoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示观察所有插件启动的观察者的实现。
 *
 * @author 李金绪
 * @since 2024/5/16
 */
@Component
public class ToolRepositoryStarter implements PluginStartedObserver, PluginStoppingObserver {
    private final ToolFactoryRepository toolFactoryRepository;

    ToolRepositoryStarter(BrokerClient brokerClient, @Fit(alias = "json") ObjectSerializer serializer,
            ToolFactoryRepository toolFactoryRepository) {
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repo cannot be null.");
        this.toolFactoryRepository.register(ToolFactory.fit(brokerClient, serializer));
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
