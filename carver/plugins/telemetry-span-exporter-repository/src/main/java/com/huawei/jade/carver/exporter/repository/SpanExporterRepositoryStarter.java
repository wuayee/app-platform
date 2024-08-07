/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.repository;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.plugin.PluginStartedObserver;
import com.huawei.fitframework.plugin.PluginStartingObserver;
import com.huawei.fitframework.plugin.PluginStoppingObserver;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.service.CarverSpanExporter;
import com.huawei.jade.service.SpanExporterRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 表示观察所有插件启动的观察者的实现， 用于注册操作单元导出器。
 *
 * @author 刘信宏
 * @since 2024-07-22
 */
@Component
public class SpanExporterRepositoryStarter
        implements PluginStartingObserver, PluginStartedObserver, PluginStoppingObserver {
    private final SpanExporterRepository exportersRepository;
    private final int exporterMaxSize;

    /**
     * 使用操作单元导出器的容器初始化 {@link SpanExporterRepositoryStarter} 对象。
     *
     * @param exportersRepository 表示操作单元导出器的 {@link SpanExporterRepository}。
     * @param exporterMaxSize 表示操作单元导出器最大数量的 {@code int}。
     */
    public SpanExporterRepositoryStarter(SpanExporterRepository exportersRepository,
            @Value("${exporter.maxSize}") int exporterMaxSize) {
        this.exportersRepository = Validation.notNull(exportersRepository, "The exporters container cannot be null.");
        this.exporterMaxSize = Validation.greaterThan(exporterMaxSize, 0,
                "The exporter max size must be positive. [maxSize={0}]", exporterMaxSize);
    }

    @Override
    public void onPluginStarting(Plugin plugin) {
        if (this.exportersRepository.get(Objects::nonNull).size() >= this.exporterMaxSize) {
            throw new IllegalStateException(StringUtils.format("The exporters cannot greater than {0}.",
                    this.exporterMaxSize));
        }
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        this.getSpanExporters(plugin).forEach(this.exportersRepository::register);
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        this.getSpanExporters(plugin).forEach(this.exportersRepository::unregister);
    }

    private List<CarverSpanExporter> getSpanExporters(Plugin plugin) {
        return plugin.container()
                .factories(CarverSpanExporter.class)
                .stream()
                .map(BeanFactory::<CarverSpanExporter>get)
                .collect(Collectors.toList());
    }
}
