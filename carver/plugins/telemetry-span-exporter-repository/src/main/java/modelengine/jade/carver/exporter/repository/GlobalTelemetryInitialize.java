/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter.repository;

import modelengine.jade.service.CarverGlobalOpenTelemetry;
import modelengine.jade.service.SpanExporterRepository;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Initialize;
import modelengine.fitframework.inspection.Validation;

import java.time.Duration;

/**
 * {@link OpenTelemetry} 全局对象初始化器。
 *
 * @author 刘信宏
 * @since 2024-10-28
 */
@Component
public class GlobalTelemetryInitialize {
    private final SpanExporterRepository exportersRepository;
    private final SpanProcessorConfig processorConfig;

    /**
     * 使用操作单元导出器的容器和配置参数初始化 {@link GlobalTelemetryInitialize} 对象。
     *
     * @param exportersRepository 表示操作单元导出器的容器的 {@link SpanExporterRepository}。
     * @param processorConfig 表示配置参数的 {@link SpanProcessorConfig}。
     */
    public GlobalTelemetryInitialize(SpanExporterRepository exportersRepository, SpanProcessorConfig processorConfig) {
        this.exportersRepository = Validation.notNull(exportersRepository, "The exporters repository cannot be null.");
        this.processorConfig = Validation.notNull(processorConfig, "The span processor config cannot be null.");
    }

    @Initialize
    private void initGlobalTelemetry() {
        SdkTracerProviderBuilder tracerBuilder = SdkTracerProvider.builder();
        tracerBuilder.addSpanProcessor(BatchSpanProcessor.builder(new SpanExporterProxy(this.exportersRepository))
                .setMaxQueueSize(this.processorConfig.getMaxQueueSize())
                .setMaxExportBatchSize(this.processorConfig.getMaxExportBatchSize())
                .setExporterTimeout(Duration.ofMillis(this.processorConfig.getExporterTimeoutMillis()))
                .setScheduleDelay(Duration.ofMillis(this.processorConfig.getScheduleDelayMillis()))
                .build());
        SdkTracerProvider sdkTracerProvider = tracerBuilder.build();
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider).build();
        CarverGlobalOpenTelemetry.set(openTelemetry);
    }
}
