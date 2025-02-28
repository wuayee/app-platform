/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter.repository;

import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

import io.opentelemetry.sdk.trace.SpanProcessor;
import lombok.Data;

/**
 * {@link SpanProcessor} 的配置参数。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
@Component
@AcceptConfigValues("span-processor")
@Data
public class SpanProcessorConfig {
    /**
     * 队列最大长度。
     */
    private int maxQueueSize;

    /**
     * 批量导出最大数量。
     */
    private int maxExportBatchSize;

    /**
     * 数据导出超时时间。
     */
    private long exporterTimeoutMillis;

    /**
     * 数据导出周期间隔时间。
     */
    private long scheduleDelayMillis;
}
