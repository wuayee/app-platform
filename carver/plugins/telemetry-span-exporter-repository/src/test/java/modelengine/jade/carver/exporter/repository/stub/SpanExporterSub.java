/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter.repository.stub;

import modelengine.fitframework.annotation.Component;
import modelengine.jade.service.CarverSpanExporter;

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
