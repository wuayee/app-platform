/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.service;

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
