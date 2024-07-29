/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.repository.stub;

import com.huawei.fitframework.annotation.Component;
import com.huawei.jade.service.CarverSpanExporter;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.SpanData;

import java.util.Collection;

/**
 * {@link CarverSpanExporter} 的打桩实现。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@Component
public class SpanExporterSub implements CarverSpanExporter {
    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public CompletableResultCode export(Collection<SpanData> collection) {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }
}
