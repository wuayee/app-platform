/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter.container;

import com.huawei.fitframework.annotation.AcceptConfigValues;
import com.huawei.fitframework.annotation.Component;

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
