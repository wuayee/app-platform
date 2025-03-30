/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

import modelengine.jade.service.CarverGlobalOpenTelemetry;
import modelengine.jade.service.CarverSpanExporter;
import modelengine.jade.service.SpanExporterRepository;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.trace.data.SpanData;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * {@link GlobalTelemetryInitialize} 的测试。
 *
 * @author 刘信宏
 * @since 2024-10-28
 */
@FitTestWithJunit(includeClasses = {GlobalTelemetryInitialize.class, DefaultSpanExporterRepository.class,
        SpanProcessorConfig.class})
public class GlobalTelemetryInitializeTest {
    @Fit
    private SpanExporterRepository repository;

    @Mock
    private SpanData spanData;

    @Mock
    private CarverSpanExporter exporter;

    @AfterEach
    void tearDown() {
        clearInvocations(this.exporter);
    }

    @Test
    @DisplayName("初始化 OpenTelemetry 全局对象成功。")
    public void shouldOkWhenGlobalTelemetryInitialCompleted() {
        assertThat(CarverGlobalOpenTelemetry.get()).isNotEqualTo(OpenTelemetry.noop());
    }

    @Test
    @DisplayName("Span导出器代理导出数据成功。")
    public void shouldOkWhenProxyExportSucceed() {
        try (SpanExporterProxy exporterProxy = new SpanExporterProxy(this.repository)) {
            repository.register(this.exporter);
            exporterProxy.export(Collections.singletonList(this.spanData));
        }
        verify(this.exporter).export(anyCollection());
        verify(this.exporter).shutdown();
    }
}
