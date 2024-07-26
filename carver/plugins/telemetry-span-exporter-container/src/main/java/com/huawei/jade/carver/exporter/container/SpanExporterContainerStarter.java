/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.container;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStoppingObserver;
import com.huawei.jade.service.CarverSpanExporter;
import com.huawei.jade.service.SpanExporterContainer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示观察所有插件启动的观察者的实现， 用于注册操作单元导出器。
 *
 * @author 刘信宏
 * @since 2024-07-22
 */
@Component
public class SpanExporterContainerStarter implements PluginStartedObserver, PluginStoppingObserver {
    private final SpanExporterContainer exportersContainer;

    /**
     * 使用操作单元导出器的容器初始化 {@link SpanExporterContainerStarter} 对象。
     *
     * @param exportersContainer 表示操作单元导出器的 {@link SpanExporterContainer}。
     */
    public SpanExporterContainerStarter(SpanExporterContainer exportersContainer) {
        this.exportersContainer = Validation.notNull(exportersContainer, "The exporters container cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        this.getSpanExporters(plugin).forEach(this.exportersContainer::register);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        this.getSpanExporters(plugin).forEach(this.exportersContainer::unregister);
    }

    private List<CarverSpanExporter> getSpanExporters(Plugin plugin) {
        return plugin.container()
                .factories(CarverSpanExporter.class)
                .stream()
                .map(BeanFactory::<CarverSpanExporter>get)
                .collect(Collectors.toList());
    }
}
