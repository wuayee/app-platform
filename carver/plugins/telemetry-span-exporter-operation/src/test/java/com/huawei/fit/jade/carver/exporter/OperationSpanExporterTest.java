/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jade.carver.exporter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.jade.carver.exporter.OperationLogExporter;
import com.huawei.jade.carver.exporter.OperationSpanExporter;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.trace.data.EventData;
import io.opentelemetry.sdk.trace.data.SpanData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

/**
 * {@link OperationSpanExporter} 的测试。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@FitTestWithJunit(classes = {OperationSpanExporter.class})
public class OperationSpanExporterTest {
    private static final String STUB_SPAN_NAME = "operation.stub";
    private static final String STUB_ATTRIBUTE_KEY = "stubAttributeKey";
    private static final String STUB_ATTRIBUTE_VALUE = "stubAttributeValue";
    private static final String EXCEPTION_EVENT_NAME = "exception";
    private static final String EXCEPTION_EVENT_MSG_KEY = "exception.message";

    @Fit
    private OperationSpanExporter spanExporter;
    @Mocked
    private OperationLogExporter logExporter;
    @Mocked
    private SpanData spanData;

    private void mockSucceedSpanData() {
        Mockito.when(this.spanData.getName()).thenReturn(STUB_SPAN_NAME);
        Mockito.when(this.spanData.getEvents()).thenReturn(Collections.emptyList());
        Mockito.when(this.spanData.getAttributes())
                .thenReturn(Attributes.of(AttributeKey.stringKey(STUB_ATTRIBUTE_KEY), STUB_ATTRIBUTE_VALUE));
    }

    private void mockFailedSpanData() {
        Mockito.when(this.spanData.getName()).thenReturn(STUB_SPAN_NAME);
        Mockito.when(this.spanData.getEvents())
                .thenReturn(Collections.singletonList(EventData.create(0, EXCEPTION_EVENT_NAME,
                        Attributes.of(AttributeKey.stringKey(EXCEPTION_EVENT_MSG_KEY), STUB_ATTRIBUTE_VALUE))));
    }

    @Test
    @DisplayName("导出成功操作的操作日志。")
    public void shouldOkWhenExportSucceedOperationLog() {
        this.mockSucceedSpanData();

        this.spanExporter.export(Collections.singletonList(this.spanData));
        verify(this.logExporter).succeed(eq(STUB_SPAN_NAME), argThat(map -> {
            assertThat(map.size()).isEqualTo(1);
            assertThat(map).containsKey(STUB_ATTRIBUTE_KEY);
            return true;
        }));
    }

    @Test
    @DisplayName("导出失败操作的操作日志。")
    public void shouldOkWhenExportFailedOperationLog() {
        this.mockFailedSpanData();

        this.spanExporter.export(Collections.singletonList(this.spanData));
        verify(this.logExporter).failed(eq(STUB_SPAN_NAME), argThat(map -> {
            assertThat(map.size()).isEqualTo(1);
            assertThat(map).containsKey(OperationLogExporter.EXCEPTION_DETAIL_KEY);
            return true;
        }));
    }
}
