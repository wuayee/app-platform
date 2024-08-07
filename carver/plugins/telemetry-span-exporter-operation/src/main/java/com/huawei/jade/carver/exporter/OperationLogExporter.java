/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.exporter;

import com.huawei.jade.carver.operation.support.CompositParam;

/**
 * 操作日志导出器。
 *
 * @author 刘信宏
 * @since 2024-07-25
 */
public interface OperationLogExporter {
    /**
     * 表示异常操作详情的键。
     */
    String EXCEPTION_DETAIL_KEY = "cause";

    /**
     * 导出成功操作日志。
     *
     * @param operation 表示操作名称的 {@link String}。
     * @param params 表示操作日志参数键值对的 {@link CompositParam}。
     */
    void succeed(String operation, CompositParam params);

    /**
     * 导出异常操作日志。
     *
     * @param operation 表示操作名称的 {@link String}。
     * @param params 表示操作日志参数键值对的 {@link CompositParam}。
     */
    void failed(String operation, CompositParam params);
}
