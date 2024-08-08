/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStoppingObserver;
import com.huawei.jade.fel.tool.ToolFactory;
import com.huawei.jade.fel.tool.ToolFactoryRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示 {@link ToolFactory} 的自动装配器。
 *
 * @author 易文渊
 * @since 2024-08-15
 */
@Component
public class ToolFactoryDiscoverer implements PluginStartedObserver, PluginStoppingObserver {
    private final ToolFactoryRepository factoryRepository;

    /**
     * 创建工具工厂的自动装配器实例。
     *
     * @param factoryRepository 表示工具工厂存储的 {@link ToolFactoryRepository}。
     */
    public ToolFactoryDiscoverer(ToolFactoryRepository factoryRepository) {
        this.factoryRepository = notNull(factoryRepository, "The tool factory repository cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        scanToolFactory(plugin).forEach(this.factoryRepository::register);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        scanToolFactory(plugin).forEach(this.factoryRepository::unregister);
    }

    private static List<ToolFactory> scanToolFactory(Plugin plugin) {
        return plugin.container()
                .factories(ToolFactory.class)
                .stream()
                .map(BeanFactory::<ToolFactory>get)
                .collect(Collectors.toList());
    }
}