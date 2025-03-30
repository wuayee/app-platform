/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter.repository;

import modelengine.jade.service.CarverSpanExporter;
import modelengine.jade.service.SpanExporterRepository;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 操作单元数据导出器的代理。
 *
 * @author 刘信宏
 * @since 2024-10-28
 */
public class SpanExporterProxy implements CarverSpanExporter {
    private static final Logger log = Logger.get(SpanExporterProxy.class);

    private final SpanExporterRepository exportersRepository;

    /**
     * 使用操作单元导出器的容器初始化 {@link SpanExporterProxy} 对象。
     *
     * @param exportersRepository 表示操作单元导出器的容器的 {@link SpanExporterRepository}。
     */
    public SpanExporterProxy(SpanExporterRepository exportersRepository) {
        this.exportersRepository = Validation.notNull(exportersRepository, "The exporters repository cannot be null.");
    }

    @Override
    public String name() {
        return "span.exporter.proxy";
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        this.proxyHandle("Export span", exporter -> exporter.export(collection));
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        this.proxyHandle("Flush span", SpanExporter::flush);
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        this.proxyHandle("Shutdown exporter", SpanExporter::shutdown);
        return CompletableResultCode.ofSuccess();
    }

    private void proxyHandle(String operation, Consumer<CarverSpanExporter> consumer) {
        this.exportersRepository.get(Objects::nonNull).forEach(exporter -> {
            try {
                consumer.accept(exporter);
            } catch (Exception exception) {
                log.warn("{} failed. [exporter={}]", operation, exporter.name(), exception);
            }
        });
    }
}
