/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.exporter;

import modelengine.jade.carver.operation.support.CompositParam;

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
     * 导出操作日志。
     *
     * @param operation 表示操作名称的 {@link String}。
     * @param params 表示操作日志参数键值对的 {@link CompositParam}。
     */
    void export(String operation, CompositParam params);
}
