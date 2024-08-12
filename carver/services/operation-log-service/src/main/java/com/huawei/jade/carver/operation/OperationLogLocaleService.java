/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.operation;

import com.huawei.jade.carver.operation.support.CompositParam;
import com.huawei.jade.carver.operation.support.OperationLogFields;

/**
 * 操作日志国际化接口。
 *
 * @author 方誉州
 * @since 2024-08-01
 */
public interface OperationLogLocaleService {
    /**
     * 获取国际化操作日志。
     *
     * @param operation 表示操作的 {@link String}。
     * @param params 表示操作日志中记录的国际化信息的参数的 {@link CompositParam}。
     * @return 表示国际化后的操作日志条目的 {@link OperationLogFields}。
     */
    OperationLogFields getLocaleMessage(String operation, CompositParam params);
}
