/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.service;

import io.opentelemetry.sdk.trace.export.SpanExporter;

/**
 * 表示操作单元导出器。
 *
 * @author 刘信宏
 * @since 2024-07-21
 */
public interface CarverSpanExporter extends SpanExporter {
    /**
     * 获取操作单元导出器的名称。
     *
     * @return 表示操作单元导出器名称的 {@link String}。
     */
    String name();
}
