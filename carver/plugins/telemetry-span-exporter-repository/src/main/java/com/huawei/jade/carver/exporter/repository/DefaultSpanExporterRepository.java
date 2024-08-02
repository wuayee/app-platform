/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.repository;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.jade.service.CarverGlobalOpenTelemetry;
import com.huawei.jade.service.CarverSpanExporter;
import com.huawei.jade.service.SpanExporterRepository;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 表示 {@link SpanExporterRepository} 的默认实现。
 *
 * @author 刘信宏
 * @since 2024-07-22
 */
@Component
public class DefaultSpanExporterRepository implements SpanExporterRepository {
    private final List<CarverSpanExporter> exporters = Collections.synchronizedList(new ArrayList<>());

    private final SpanProcessorConfig processorConfig;

    /**
     * 使用配置参数初始化 {@link DefaultSpanExporterRepository} 对象。
     *
     * @param processorConfig 表示配置参数的 {@link SpanProcessorConfig}。
     */
    public DefaultSpanExporterRepository(SpanProcessorConfig processorConfig) {
        this.processorConfig = Validation.notNull(processorConfig, "The span processor config cannot be null.");
    }

    @Override
    public void register(CarverSpanExporter exporter) {
        if (exporter != null) {
            this.exporters.add(exporter);
            this.resetGlobalTelemetry();
        }
    }

    @Override
    public void unregister(CarverSpanExporter exporter) {
        if (exporter != null && this.exporters.contains(exporter)) {
            this.exporters.remove(exporter);
            this.resetGlobalTelemetry();
        }
    }

    @Override
    public List<CarverSpanExporter> get(Predicate<CarverSpanExporter> predicate) {
        Validation.notNull(predicate, "The filter condition cannot be null.");
        return this.exporters.stream().filter(predicate).collect(Collectors.toList());
    }

    private void resetGlobalTelemetry() {
        SdkTracerProviderBuilder tracerBuilder = SdkTracerProvider.builder();
        this.get(Objects::nonNull)
                .forEach(exporter -> tracerBuilder.addSpanProcessor(BatchSpanProcessor.builder(exporter)
                        .setMaxQueueSize(this.processorConfig.getMaxQueueSize())
                        .setMaxExportBatchSize(this.processorConfig.getMaxExportBatchSize())
                        .setExporterTimeout(Duration.ofMillis(this.processorConfig.getExporterTimeoutMillis()))
                        .setScheduleDelay(Duration.ofMillis(this.processorConfig.getScheduleDelayMillis()))
                        .build()));
        SdkTracerProvider sdkTracerProvider = tracerBuilder.build();
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).build();
        CarverGlobalOpenTelemetry.set(openTelemetry);
    }
}
